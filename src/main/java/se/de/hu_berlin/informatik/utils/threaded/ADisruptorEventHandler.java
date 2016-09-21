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
public abstract class ADisruptorEventHandler<T> implements EventHandler<Event<T>> {

    private DisruptorProvider<T> callback = null;
    
    /**
     * Creates a {@link ADisruptorEventHandler}.
     */
    public ADisruptorEventHandler() {
        super();
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
     * @return
     * the callback disruptor provider
     */
    protected DisruptorProvider<T> getCallback() {
    	return callback;
    }
    
    @Override
    public void onEvent(Event<T> event, long sequence, boolean endOfBatch) throws Exception {
//		Log.out(this, event.get().toString() + " " + sequence);
    	try {
    		processEvent(event.get());
    	} catch (Throwable t) {
    		Log.err(this, t, "An error occurred while processing item '%s'.", event.get());
    	}
    	if (callback != null) {
    		callback.onEventEnd();
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
