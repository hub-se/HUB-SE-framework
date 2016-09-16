package se.de.hu_berlin.informatik.utils.threaded;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;

public class DisruptorProvider<T> {

	private static ThreadFactory threadFactory;
	private static Thread mainThread;
	
	static {
		//thread factory that will be used to construct new threads for consumers
		threadFactory = Executors.defaultThreadFactory();
		mainThread = Thread.currentThread();
	}
	
	//holds the amount of pending items that were submitted but not yet processed
	private AtomicInteger pendingItems = new AtomicInteger(0); 
	
	private Disruptor<Event<T>> disruptor = null;
	private RingBuffer<Event<T>> ringBuffer = null;
	private DisruptorEventHandler<T>[] handlers = null;
	private int bufferSize;
	
	private boolean isRunning = false;
	private boolean isConnectedToHandlers = false;
	
	public DisruptorProvider(int bufferSize) {
		super();
		this.bufferSize = bufferSize;
		if (Integer.bitCount(bufferSize) != 1) {
            throw new IllegalArgumentException("bufferSize must be a power of 2");
        }
	}

	private void createNewDisruptorInstance() {
		// Construct the Disruptor
		disruptor = new Disruptor<>(Event<T>::new, bufferSize, threadFactory,
				ProducerType.MULTI, new BlockingWaitStrategy());

		// Get the ring buffer from the Disruptor to be used for publishing.
		ringBuffer = disruptor.getRingBuffer();
	}
	
	public Disruptor<Event<T>> getOrCreateDisruptor() {
		if (disruptor == null) {
			createNewDisruptorInstance();
		}
		return disruptor;
	}
	
	public RingBuffer<Event<T>> getOrCreateRingBuffer() {
		if (ringBuffer == null) {
			createNewDisruptorInstance();
		}
		return ringBuffer;
	}
	
	public boolean isRunning() {
		return isRunning;
	}
	
	private DisruptorProvider<T> cleanup() {
		if (!isRunning) {
			disruptor = null;
			ringBuffer = null;
			isConnectedToHandlers = false;
		}
		return this;
	}
	
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
	
	public DisruptorProvider<T> connectHandlers(int numberOfThreads, Class<? extends DisruptorEventHandler<T>> eventHandlerClass, Object... constructorArguments) {
		if (isConnectedToHandlers) {
			throw new IllegalStateException("Already connected to handlers.");
		}
		Class<?>[] typeArgs = eventHandlerClass.getConstructors()[0].getParameterTypes();//TODO is that right?
		
		Log.out(this, Misc.arrayToString(typeArgs));
		// Use Array native method to create array
        // of a type only known at run time
        @SuppressWarnings("unchecked")
        final DisruptorEventHandler<T>[] handlers = (DisruptorEventHandler<T>[]) Array.newInstance(eventHandlerClass, numberOfThreads);
        
		for (int i = 0; i < numberOfThreads; ++i) {
			DisruptorEventHandler<T> o;
			try {
				o = eventHandlerClass.getConstructor(typeArgs).newInstance(constructorArguments);
				handlers[i] = o;
			} catch (InstantiationException e) {
				Log.abort(this, e, "Cannot instantiate object %s.", eventHandlerClass.getSimpleName());
			} catch (IllegalAccessException e) {
				Log.abort(this, e, "Illegal access to object %s.", eventHandlerClass.getSimpleName());
			} catch (IllegalArgumentException e) {
				Log.abort(this, e, "Illegal argument to object %s.", eventHandlerClass.getSimpleName());
			} catch (InvocationTargetException e) {
				Log.abort(this, e, "Invocation target exception on object %s.", eventHandlerClass.getSimpleName());
			} catch (NoSuchMethodException e) {
				Log.abort(this, e, "No such method exception on object %s.", eventHandlerClass.getSimpleName());
			} catch (SecurityException e) {
				Log.abort(this, e, "Security exception on object %s.", eventHandlerClass.getSimpleName());
			}
		}
		
		connectHandlers(handlers);
		
		return this;
	}
	
	public DisruptorProvider<T> start() {
		startIfNotRunning();
		return this;
	}
	
	private synchronized void startIfNotRunning() {
		if (!isRunning) {
			if (disruptor == null) {
				if (handlers == null) {
					throw new IllegalStateException("No handlers given. Cannot start the disruptor.");
				}
				createNewDisruptorInstance();
				connectHandlers(handlers);
			}

			if (!isConnectedToHandlers) {
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
	
	public DisruptorProvider<T> waitForPendingEventsToFinish() {
		if (disruptor != null && isRunning) {
			//wait for pending operations to finish
			while (pendingItems.get() > 0) {
				LockSupport.park();
			}
		}
		return this;
	}
	
	public DisruptorProvider<T> shutdown() {
		waitForPendingEventsToFinish();
		if (disruptor != null && isRunning) {
			// Shuts down the disruptor
			disruptor.shutdown();
			isRunning = false;
		}
		cleanup();
		return this;
	}
	
	public void submit(T item) {
		startIfNotRunning();
		pendingItems.incrementAndGet();
		ringBuffer.publishEvent(Event::translate, item);
	}

	public void onEventEnd() {
		if(pendingItems.decrementAndGet() <= 0) {
			LockSupport.unpark(mainThread);
		}
	}
}
