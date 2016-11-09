package se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler;

import com.lmax.disruptor.EventHandler;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.threaded.ThreadLimit;
import se.de.hu_berlin.informatik.utils.threaded.ThreadLimitDummy;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.DisruptorProvider;

/**
 * Abstract event handler that is used by a {@link DisruptorProvider}.
 * 
 * <p> This implementation may further restrict the total number of running threads
 * in the application by setting an {@link ThreadLimit} object. This will probably come
 * with additional synchronization cost, of course, 
 * but limits the amount of parallel threads to a set number. If no thread limit object
 * is set, then access to threads will be unrestricted with practically no additional 
 * costs (no synchronization will take place). 
 * 
 * @author Simon Heiden
 * @param <A>
 * the type of elements that shall be processed by this handler
 * @see DisruptorProvider
 */
public abstract class AbstractDisruptorEventHandler<A> implements EventHandler<SingleUseEvent<A>> {

    private DisruptorProvider<A> callback = null;
    private ThreadLimit limit = ThreadLimitDummy.getInstance();
	private boolean singleConsumer = false;
    
    /**
     * Creates a {@link AbstractDisruptorEventHandler}.
     * @param isSingle
     * whether this consumer is the only one which is
     * connected to the disruptor
     */
    public AbstractDisruptorEventHandler(boolean isSingle) {
        super();
        singleConsumer = isSingle;
    }
    
    /**
     * Creates a {@link AbstractDisruptorEventHandler} which
     * is set to be not a single producer
     */
    public AbstractDisruptorEventHandler() {
        super();
        singleConsumer = false;
    }
    
    /**
     * Sets a disruptor provider instance as a callback.
     * @param callback
     * a disruptor provider
     */
    public void setCallback(DisruptorProvider<A> callback) {
    	this.callback = callback;
    }
    
    public void setThreadLimit(ThreadLimit limit) {
    	this.limit = limit;
    }
    
    /**
     * @return
     * the callback disruptor provider
     */
    protected DisruptorProvider<A> getCallback() {
    	return callback;
    }
    
    @Override
    public void onEvent(SingleUseEvent<A> event, long sequence, boolean endOfBatch) throws Exception {
    	limit.acquireSlot();
//		Log.out(this, event.get().toString() + " " + sequence);
    	try {
    		resetAndInit();
    		processEvent(event.get());
    	} catch (Throwable t) {
    		Log.err(this, t, "An error occurred while processing item '%s'.", event.get());
    	}
//    	if (callback != null) {
//    		callback.onEventEnd();
//    	}
    	limit.releaseSlot();
	}
	
    /**
     * @param isSingle
     * whether this consumer is the only one which is
     * connected to the disruptor
     */
    public void setSingleConsumer(boolean isSingle) {
    	singleConsumer  = isSingle;
    }
    
    protected boolean isSingleConsumer() {
		return singleConsumer;
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
	public abstract void processEvent(A input) throws Exception;
	
	/**
	 * Should be used to reset or to initialize fields. Gets called before processing each event.
	 */
	public abstract void resetAndInit(); 
    
}
