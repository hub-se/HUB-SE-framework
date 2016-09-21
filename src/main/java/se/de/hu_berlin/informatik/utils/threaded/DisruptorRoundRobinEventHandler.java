package se.de.hu_berlin.informatik.utils.threaded;

/**
 * Abstract event handler that is used by a {@link DisruptorProvider}. Uses a
 * "round robin" strategy to assign a handler to a single event. An event with a
 * specific sequence number will only be processed by a single, unique event handler
 * which may block other handlers from proceeding, even if they are idle and there
 * are pending events that could be processed.
 * 
 * @author Simon Heiden
 * @param <T>
 * the type of elements that shall be processed by this handler
 * @see DisruptorProvider
 */
public abstract class DisruptorRoundRobinEventHandler<T> extends ADisruptorEventHandler<T> {

	private long ordinal;
    private long numberOfConsumers;
    
    private boolean single = false;

    /**
     * Creates a {@link DisruptorRoundRobinEventHandler}.
     */
    public DisruptorRoundRobinEventHandler() {
        this(-1, -1);
    }
    
    /**
     * Creates a {@link DisruptorRoundRobinEventHandler}.
     * @param ordinal
     * the index of this handler
     * @param numberOfConsumers
     * the total number of consumers
     */
    public DisruptorRoundRobinEventHandler(long ordinal, long numberOfConsumers) {
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
			super.onEvent(event, sequence, endOfBatch);
        }
	}
	
}
