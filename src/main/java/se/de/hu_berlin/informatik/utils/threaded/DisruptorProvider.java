package se.de.hu_berlin.informatik.utils.threaded;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;
import se.de.hu_berlin.informatik.utils.tracking.Trackable;

/**
 * Provides convenient creation and access tools for a disruptor.
 * 
 * @author Simon Heiden
 * @param <T>
 * the type of items that may be submitted and processed by the disruptor
 */
public class DisruptorProvider<T> extends Trackable {

	private static ThreadFactory threadFactory;
	private Thread mainThread;
	
	static {
		//thread factory that will be used to construct new threads for consumers
		threadFactory = Executors.defaultThreadFactory();
	}
	
	//holds the amount of pending items that were submitted but not yet processed
	private AtomicInteger pendingItems = new AtomicInteger(0); 
	
	private Disruptor<Event<T>> disruptor = null;
	private RingBuffer<Event<T>> ringBuffer = null;
	private DisruptorEventHandler<T>[] handlers = null;
	private int bufferSize;
	
	private ProducerType producerType = ProducerType.MULTI;
	
	private boolean isRunning = false;
	private boolean isConnectedToHandlers = false;
	
	/**
	 * Creates a new disruptor provider with the given buffer size.
	 * @param bufferSize
	 * the buffer size (must be a power of 2)
	 */
	public DisruptorProvider(int bufferSize) {
		super();
		this.bufferSize = bufferSize;
		if (Integer.bitCount(bufferSize) != 1) {
            throw new IllegalArgumentException("bufferSize must be a power of 2");
        }
		mainThread = Thread.currentThread();
	}

	/**
	 * Creates a new disruptor instance.
	 */
	private void createNewDisruptorInstance() {
		// Construct the Disruptor
		disruptor = new Disruptor<>(Event<T>::new, bufferSize, threadFactory,
				producerType, new BlockingWaitStrategy());

		// Get the ring buffer from the Disruptor to be used for publishing.
		ringBuffer = disruptor.getRingBuffer();
	}
	
	/**
	 * Returns the disruptor instance. Creates one if
	 * none exists.
	 * @return
	 * the disruptor instance
	 */
	public Disruptor<Event<T>> getOrCreateDisruptor() {
		if (disruptor == null) {
			createNewDisruptorInstance();
		}
		return disruptor;
	}
	
	/**
	 * Returns the ring buffer of a disruptor instance. Creates one if
	 * none exists.
	 * @return
	 * the ring buffer
	 */
	public RingBuffer<Event<T>> getOrCreateRingBuffer() {
		if (ringBuffer == null) {
			createNewDisruptorInstance();
		}
		return ringBuffer;
	}
	
	/**
	 * @return
	 * the associated handlers (may be null if not yet specified)
	 */
	public DisruptorEventHandler<T>[] getHandlers() {
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
	private DisruptorProvider<T> cleanup() {
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
	public void connectHandlers(@SuppressWarnings("unchecked") DisruptorEventHandler<T>... handlers) {
		if (disruptor == null) {
			createNewDisruptorInstance();
		} else if (isConnectedToHandlers) {
			throw new IllegalStateException("Already connected to handlers.");
		}
		
		if (handlers == null || handlers.length <= 0) {
			throw new IllegalStateException("No Handlers given.");
		}
		
		for (int i = 0; i < handlers.length; ++i) {
			handlers[i].setIndex(i);
			handlers[i].setNumberOfConsumers(handlers.length);
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
	public DisruptorProvider<T> connectHandlers(int numberOfThreads, IDisruptorEventHandlerFactory<T> factory) {
		if (isConnectedToHandlers) {
			throw new IllegalStateException("Already connected to handlers.");
		}
		
		//create generic array for the handlers to be instantiated
        final DisruptorEventHandler<T>[] handlers = Misc.createGenericArray(factory.getEventHandlerClass(), numberOfThreads);
        
        //instantiate the desired number of handlers
		for (int i = 0; i < numberOfThreads; ++i) {
			handlers[i] = factory.newInstance();
		}
		
		//connect the handlers to the disruptor
		connectHandlers(handlers);
		
		return this;
	}
	
	/**
	 * Starts the disruptor manually. This is normally not needed.
	 * @return
	 * this
	 */
	public DisruptorProvider<T> start() {
		//avoid synchronized method call if already running
		if (!isRunning) {
			startIfNotRunning();
		}
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
	public DisruptorProvider<T> waitForPendingEventsToFinish() {
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
	public DisruptorProvider<T> shutdown() {
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
	public void submit(T item) {
		//avoid synchronized method call if already running
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
	protected void onEventEnd() {
		if(pendingItems.decrementAndGet() <= 0) {
			LockSupport.unpark(mainThread);
		}
	}
}
