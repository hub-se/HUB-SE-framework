package se.de.hu_berlin.informatik.utils.threaded;

import com.lmax.disruptor.EventHandler;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;

/**
 * Abstract event handler that is used by a {@link DisruptorProvider}.
 * 
 * @author Simon Heiden
 * @param <T>
 * the type of elements that shall be processed by this handler
 * @see DisruptorProvider
 */
public abstract class DisruptorEventHandler<T> implements EventHandler<Event<T>> {

	private long ordinal;
    private long numberOfConsumers;
    
    private DisruptorProvider<T> callback = null;
    
    private boolean single = false;

    /**
     * Creates a {@link DisruptorEventHandler}.
     */
    public DisruptorEventHandler() {
        this(-1, -1);
    }
    
    /**
     * Creates a {@link DisruptorEventHandler}.
     * @param ordinal
     * the index of this handler
     * @param numberOfConsumers
     * the total number of consumers
     */
    public DisruptorEventHandler(long ordinal, long numberOfConsumers) {
    	super();
        this.ordinal = ordinal;
        this.numberOfConsumers = numberOfConsumers;
        if (numberOfConsumers == 1) {
    		single = true;
    	} else {
    		single = false;
    	}
    }
    
    /**
     * Sets the index of this handler.
     * @param ordinal
     * the index
     */
    protected void setIndex(long ordinal) {
    	this.ordinal = ordinal;
    }
    
    /**
     * Sets a disruptor provider instance as a callback.
     * @param callback
     * a disruptor provider
     */
    protected void setCallback(DisruptorProvider<T> callback) {
    	this.callback = callback;
    }
    
    /**
     * Sets the total number of parallel handlers.
     * @param numberOfConsumers
     * the total number of consumers
     */
    protected void setNumberOfConsumers(long numberOfConsumers) {
    	this.numberOfConsumers = numberOfConsumers;
    	if (numberOfConsumers == 1) {
    		single = true;
    	} else {
    		single = false;
    	}
    }
    
	@Override
	public void onEvent(Event<T> event, long sequence, boolean endOfBatch) throws Exception {
		if (single || (sequence % numberOfConsumers) == ordinal) {
//			Log.out(this, event.get().toString() + " " + sequence);
			try {
				processEvent(event.get());
			} catch (Throwable t) {
				Log.err(this, t, "An error occurred while processing item '%s'.", event.get());
			}
			if (callback != null) {
				callback.onEventEnd();
			}
        }
	}
	
	/**
	 * Processes a single item that is provided by an event. Has to be implemented
	 * by extending classes.
	 * @param input
	 * the item to be processed
	 * @throws Exception
	 * if an error occurs. Any exception gets caught and produces an error message.
	 * This doesn't abort execution.
	 */
	abstract public void processEvent(T input) throws Exception;
    
}
