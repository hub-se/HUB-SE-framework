package se.de.hu_berlin.informatik.utils.threaded.disruptor;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;
import se.de.hu_berlin.informatik.utils.threaded.ThreadLimit;
import se.de.hu_berlin.informatik.utils.threaded.ThreadLimitDummy;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.AbstractDisruptorEventHandler;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.DisruptorFCFSEventHandler;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInputAndReturn;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.Event;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.SingleUseEvent;
import se.de.hu_berlin.informatik.utils.tm.user.ConsumingProcessorUserGenerator;
import se.de.hu_berlin.informatik.utils.tm.user.ProcessorUserGenerator;
import se.de.hu_berlin.informatik.utils.tracking.Trackable;
import se.de.hu_berlin.informatik.utils.tracking.TrackingStrategy;
import se.de.hu_berlin.informatik.utils.tracking.TrackerDummy;

/**
 * Provides convenient creation and access tools for a disruptor.
 * 
 * @author Simon Heiden
 * @param <A>
 * the type of items that may be submitted and processed by the disruptor
 */
public class DisruptorProvider<A> implements Trackable {

	final private static ThreadFactory THREAD_FACTORY;
//	private Thread mainThread;
	
	static {
		//thread factory that will be used to construct new threads for consumers
		THREAD_FACTORY = Executors.defaultThreadFactory();
//				new ThreadFactory() {
//			
//			ThreadFactory factory = Executors.defaultThreadFactory();
//			int counter = 0;
//			
//			@Override
//			public Thread newThread(Runnable r) {
//				++counter;
//				Log.out(this, "Creating Thread no. %d for %s.", counter, r);
//				return factory.newThread(r);
//			}
//		};
	}
	
	private Disruptor<SingleUseEvent<A>> disruptor = null;
	private RingBuffer<SingleUseEvent<A>> ringBuffer = null;
	private AbstractDisruptorEventHandler<A>[] handlers = null;
	private int bufferSize = 0;
	
	private ProducerType producerType = ProducerType.MULTI;
	
	private boolean abortOnEventError = false;
	private boolean abortOnStartupError = false;
	private boolean abortOnShutdownError = false;
	
	private int exceptions;
	
	private boolean isRunning = false;
	private boolean isConnectedToHandlers = false;
	private int minimalBufferSize = 0;
	
	private TrackingStrategy tracker = TrackerDummy.getInstance();
	private boolean onlyForced = false;

	/**
	 * Creates a new disruptor provider with the minimal given buffer size. 
	 * The actual buffer size will be set to m, where m is a power of 2 such that
	 * <p> {@code m >= 8*#handlers}, if {@code  minimalBufferSize < 8*#handlers}, and
	 * <p> {@code m >= minimalBufferSize}, otherwise.
	 * <p> This means that the buffer size is at least eight times as big as the 
	 * number ot handlers, but at least as big as the specified minimal buffer size
	 * @param minimalBufferSize
	 * a minimal buffer size
	 */
	public DisruptorProvider(int minimalBufferSize) {
		super();
		this.minimalBufferSize  = minimalBufferSize;
//		mainThread = Thread.currentThread();
	}
	
	/**
	 * Creates a new disruptor provider with a minimal buffer size of 8.
	 */
	public DisruptorProvider() {
		this(8);
	}

	private int getContainingPowerOfTwo(int value) {
		if (value > (int)Math.pow(2,30)) {
			return (int)Math.pow(2,30);
		}
		if (value < 0) {
			throw new IllegalStateException("Buffer size must be positive.");
		}
		return value > 1 ? Integer.highestOneBit(value-1) << 1 : 1;
	}
	
	/**
	 * Creates a new disruptor instance.
	 */
	private void createNewDisruptorInstance() {
		// Construct the Disruptor
		disruptor = new Disruptor<>(SingleUseEvent<A>::new, bufferSize, THREAD_FACTORY,
				producerType, new BlockingWaitStrategy());

		disruptor.setDefaultExceptionHandler(new ExceptionHandler<Event<A>>() {
			@Override
			public void handleOnStartException(Throwable ex) {
				if (abortOnStartupError) {
					Log.abort(this, ex, "%s was thrown while starting.", ex);
				} else {
					Log.err(this, ex, "%s was thrown while starting.", ex);
				}
			}
			@Override
			public void handleOnShutdownException(Throwable ex) {
				if (abortOnShutdownError) {
					Log.abort(this, ex, "%s was thrown while shutting down.", ex);
				} else {
					Log.err(this, ex, "%s was thrown while shutting down.", ex);
				}
			}
			@Override
			public void handleEventException(Throwable ex, long sequence, Event<A> event) {
				++exceptions;
				if (abortOnEventError) {
					Log.abort(this, ex, "%s was thrown while processing item #%d.", ex, sequence);
				} else {
					Log.err(this, ex, "%s was thrown while processing item #%d.", ex, sequence);
				}
			}
		});
		// Get the ring buffer from the Disruptor to be used for publishing.
		ringBuffer = disruptor.getRingBuffer();
	}
	
	public DisruptorProvider<A> abortOnStartupError() {
		abortOnStartupError = true;
		return this;
	}
	
	public DisruptorProvider<A> abortOnShutdownError() {
		abortOnShutdownError = true;
		return this;
	}
	
	public DisruptorProvider<A> abortOnEventError() {
		abortOnEventError = true;
		return this;
	}
	
	/**
	 * @return
	 * the associated handlers (may be null if not yet specified)
	 */
	public AbstractDisruptorEventHandler<A>[] getHandlers() {
		return handlers;
	}
	
	/**
	 * @param singleWriter
	 * whether only a single thread is writing to this disruptor
	 */
	public void setProducerType(boolean singleWriter) {
		if (singleWriter) {
//			Log.out(this, "single");
			producerType = ProducerType.SINGLE;
		} else {
//			Log.out(this, "multi");
			producerType = ProducerType.MULTI;
		}
	}
	
	/**
	 * @return
	 * whether the disruptor is running
	 */
	public boolean isRunning() {
		return isRunning;
	}
	
	/**
	 * Performs a cleanup in the sense that an existing disruptor instance gets
	 * nullified. Possibly existing instances of event handlers are kept, though,
	 * in order to be able to start the disruptor again, if needed.
	 * @return
	 * this
	 */
	private DisruptorProvider<A> cleanup() {
		if (!isRunning) {
			disruptor = null;
			ringBuffer = null;
			isConnectedToHandlers = false;
		}
		return this;
	}
	
	/**
	 * Connects the given event handlers to the disruptor. The handlers process submitted events
	 * in parallel. If no disruptor instance is available, a new one is created beforehand.
	 * @param handlers
	 * the handlers to connect
	 */
	public void connectHandlers(@SuppressWarnings("unchecked") AbstractDisruptorEventHandler<A>... handlers) {
		if (handlers == null || handlers.length <= 0) {
			throw new IllegalStateException("No Handlers given.");
		}
		
		if (disruptor == null) {
			//get a reasonable buffer size, such that it is at least eight times
			//as big as the number of handlers, but at least as big as the
			//specified minimal buffer size
			bufferSize = getContainingPowerOfTwo(handlers.length * 8);
			if (bufferSize < minimalBufferSize) {
				bufferSize = getContainingPowerOfTwo(minimalBufferSize);
			}
//			Log.out(this, "buffer size: %d", bufferSize);
			if (Integer.bitCount(bufferSize) != 1) {
				throw new IllegalArgumentException("Buffer size must be a power of 2.");
			}
			createNewDisruptorInstance();
		} else if (isConnectedToHandlers) {
			throw new IllegalStateException("Already connected to handlers.");
		}
		
		boolean isSingle = handlers.length == 1;
		for (int i = 0; i < handlers.length; ++i) {
			handlers[i].setSingleConsumer(isSingle);
		}
		// Connect the handlers
		disruptor.handleEventsWith(handlers);
		this.handlers = handlers;
		
		isConnectedToHandlers = true;
	}
	
	public <B> DisruptorProvider<A> connectHandlers(ProcessorUserGenerator<A, B> transmitter, 
			int numberOfThreads, AbstractDisruptorMultiplexer<B> multiplexer) {
		return connectHandlers(transmitter, numberOfThreads, ThreadLimitDummy.getInstance(), multiplexer);
	}
	
	/**
	 * Connects the given number of event handlers to the disruptor. The handlers are
	 * instantiated with the given transmitter.
	 * @param transmitter
	 * the transmitter to get new event handler instances from
	 * @param numberOfThreads
	 * the number of handlers (threads) to create
	 * @param limit
	 * a thread limit object
	 * @param multiplexer
	 * the multiplexer to connect the event handlers to
	 * @return
	 * this
	 * @param <B>
	 * the output type of the given transmitter
	 */
	public <B> DisruptorProvider<A> connectHandlers(ProcessorUserGenerator<A, B> transmitter, int numberOfThreads, 
			ThreadLimit limit, AbstractDisruptorMultiplexer<B> multiplexer) {
		if (isConnectedToHandlers) {
			throw new IllegalStateException("Already connected to handlers.");
		}
		if (numberOfThreads < 1) {
			throw new IllegalArgumentException("Number of threads has to be at least 1.");
		}
		
		EHWithInputAndReturn<A, B> firstEH = transmitter.newEHInstance();
		
		@SuppressWarnings("unchecked")
		Class<EHWithInputAndReturn<A,B>> clazz = (Class<EHWithInputAndReturn<A, B>>) firstEH.getClass();
		//create generic array for the handlers to be instantiated
        final EHWithInputAndReturn<A,B>[] handlers = Misc.createGenericArray(clazz, numberOfThreads);
		
        firstEH.setMultiplexer(multiplexer);
		firstEH.setThreadLimit(limit);
        handlers[0] = firstEH;
        //instantiate the desired number of handlers
		for (int i = 1; i < numberOfThreads; ++i) {
			EHWithInputAndReturn<A, B> nextEH = transmitter.newEHInstance();
			nextEH.setMultiplexer(multiplexer);
			nextEH.setThreadLimit(limit);
			handlers[i] = nextEH;
		}
		
		//connect the handlers to the disruptor
		connectHandlers(handlers);
		
		return this;
	}
	
	public DisruptorProvider<A> connectHandlers(ConsumingProcessorUserGenerator<A> consumer, int numberOfThreads) {
		return connectHandlers(consumer, numberOfThreads, ThreadLimitDummy.getInstance());
	}
	
	public DisruptorProvider<A> connectHandlers(ConsumingProcessorUserGenerator<A> consumer, int numberOfThreads, ThreadLimit limit) {
		if (isConnectedToHandlers) {
			throw new IllegalStateException("Already connected to handlers.");
		}
		if (numberOfThreads < 1) {
			throw new IllegalArgumentException("Number of threads has to be at least 1.");
		}
		
		DisruptorFCFSEventHandler<A> firstEH = consumer.newEHInstance();
		
		@SuppressWarnings("unchecked")
		Class<DisruptorFCFSEventHandler<A>> clazz = (Class<DisruptorFCFSEventHandler<A>>) firstEH.getClass();
		//create generic array for the handlers to be instantiated
        final DisruptorFCFSEventHandler<A>[] handlers = Misc.createGenericArray(clazz, numberOfThreads);
		
		firstEH.setThreadLimit(limit);
        handlers[0] = firstEH;
        //instantiate the desired number of handlers
		for (int i = 1; i < numberOfThreads; ++i) {
			DisruptorFCFSEventHandler<A> nextEH = consumer.newEHInstance();
			nextEH.setThreadLimit(limit);
			handlers[i] = nextEH;
		}
		
		//connect the handlers to the disruptor
		connectHandlers(handlers);
		
		return this;
	}
	
	/**
	 * Starts the disruptor if it is not already running.
	 */
	private synchronized void startIfNotRunning() {
		if (!isRunning) {
			if (disruptor == null || !isConnectedToHandlers) {
				if (handlers == null) {
					throw new IllegalStateException("No handlers given. Cannot start the disruptor.");
				}
				connectHandlers(handlers);
			}

			// Start the Disruptor, starts all threads running
			disruptor.start();
			isRunning = true;
		}
	}
	
	/**
	 * Shuts down the disruptor.
	 * @return
	 * this
	 */
	public DisruptorProvider<A> shutdown() {
		if (disruptor != null && isRunning) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// do nothing
			}
//			Log.out(this, "shutting down disruptor..., %s", Thread.currentThread());
			// Shuts down the disruptor
			disruptor.shutdown();
			
			isRunning = false;
			if (exceptions > 0) {
				Log.warn(this, "%d event(s) ended by throwing an exception.", exceptions);
			}
		}
		cleanup();
		return this;
	}
	
	/**
	 * Submits an item to the disruptor. Starts the disruptor threads
	 * if it is not running. If no handlers are connected, then this
	 * will throw an exception.
	 * @param item
	 * the item to submit
	 */
	public synchronized void submit(A item) {
		//avoid calling synchronized method call if already running
		if (!isRunning) {
			startIfNotRunning();
		}
		track();
//		Log.out(this, "%s, submitting %s", Thread.currentThread(), item);
		ringBuffer.publishEvent(Event::translate, item);
	}

	@Override
	public TrackingStrategy getTracker() {
		return tracker;
	}

	@Override
	public void setTracker(TrackingStrategy tracker) {
		this.tracker = tracker;
	}

	@Override
	public boolean onlyForced() {
		return onlyForced ;
	}

	@Override
	public void allowOnlyForcedTracks() {
		onlyForced = true;
	}
}
