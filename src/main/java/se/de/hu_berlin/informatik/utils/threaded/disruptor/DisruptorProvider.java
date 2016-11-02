package se.de.hu_berlin.informatik.utils.threaded.disruptor;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.AbstractDisruptorEventHandler;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.DisruptorEventHandlerFactory;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.Event;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.SingleUseEvent;
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
	private Thread mainThread;
	
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
	
	//holds the amount of pending items that were submitted but not yet processed
	private AtomicInteger pendingItems = new AtomicInteger(0); 
	
	private Disruptor<SingleUseEvent<A>> disruptor = null;
	private RingBuffer<SingleUseEvent<A>> ringBuffer = null;
	private AbstractDisruptorEventHandler<A>[] handlers = null;
	private int bufferSize = 0;
	
	private ProducerType producerType = ProducerType.MULTI;
	
	private boolean isRunning = false;
	private boolean isConnectedToHandlers = false;
	private int minimalBufferSize = 0;
	
	private TrackingStrategy tracker = TrackerDummy.getInstance();

	/**
	 * Creates a new disruptor provider with the minimal given buffer size. 
	 * The actual buffer size will be set to m, where m is a power of 2 such that
	 * <p> {@code m >= 4*#handlers}, if {@code  minimalBufferSize < 4*#handlers}, and
	 * <p> {@code m >= minimalBufferSize}, otherwise.
	 * <p> This means that the buffer size is at least four times as big as the 
	 * number ot handlers, but at least as big as the specified minimal buffer size
	 * @param minimalBufferSize
	 * a minimal buffer size
	 */
	public DisruptorProvider(int minimalBufferSize) {
		super();
		this.minimalBufferSize  = minimalBufferSize;
		mainThread = Thread.currentThread();
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

		// Get the ring buffer from the Disruptor to be used for publishing.
		ringBuffer = disruptor.getRingBuffer();
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
			//get a reasonable buffer size, such that it is at least four times
			//as big as the number of handlers, but at least as big as the
			//specified minimal buffer size
			bufferSize = getContainingPowerOfTwo(handlers.length * 4);
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
			handlers[i].setCallback(this);
		}
		// Connect the handlers
		disruptor.handleEventsWith(handlers);
		this.handlers = handlers;
		
		isConnectedToHandlers = true;
	}
	
	/**
	 * Connects the given number of event handlers to the disruptor. The handlers are
	 * instantiated with the given factory.
	 * @param numberOfThreads
	 * the number of handlers (threads) to create
	 * @param factory
	 * the factory to create the event handlers with
	 * @return
	 * this
	 */
	public DisruptorProvider<A> connectHandlers(int numberOfThreads, DisruptorEventHandlerFactory<A> factory) {
		if (isConnectedToHandlers) {
			throw new IllegalStateException("Already connected to handlers.");
		}
		
		//create generic array for the handlers to be instantiated
        final AbstractDisruptorEventHandler<A>[] handlers = Misc.createGenericArray(factory.getEventHandlerClass(), numberOfThreads);
		
        //instantiate the desired number of handlers
		for (int i = 0; i < numberOfThreads; ++i) {
			handlers[i] = factory.newInstance();
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
	 * Waits for any pending events to be processed.
	 * @return
	 * this
	 */
	public DisruptorProvider<A> waitForPendingEventsToFinish() {
		if (disruptor != null && isRunning) {
//			Log.out(this, "waiting for pending items...");
			//wait for pending operations to finish
			while (pendingItems.get() > 0) {
				LockSupport.park();
			}
		}
		return this;
	}
	
	/**
	 * Shuts down the disruptor.
	 * @return
	 * this
	 */
	public DisruptorProvider<A> shutdown() {
		waitForPendingEventsToFinish();
		if (disruptor != null && isRunning) {
//			Log.out(this, "shutting down...");
			// Shuts down the disruptor
			disruptor.shutdown();
			isRunning = false;
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
	public void submit(A item) {
		//avoid calling synchronized method call if already running
		if (!isRunning) {
			startIfNotRunning();
		}
		pendingItems.incrementAndGet();
		track();
		ringBuffer.publishEvent(Event::translate, item);
	}

	/**
	 * Gets called for each processed event at the end.
	 */
	public void onEventEnd() {
		if(pendingItems.decrementAndGet() <= 0) {
			LockSupport.unpark(mainThread);
		}
	}

	@Override
	public TrackingStrategy getTracker() {
		return tracker;
	}

	@Override
	public void setTracker(TrackingStrategy tracker) {
		this.tracker = tracker;
	}
}
