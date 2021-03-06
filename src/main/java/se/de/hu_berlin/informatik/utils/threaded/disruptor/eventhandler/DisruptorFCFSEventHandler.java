package se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler;

import se.de.hu_berlin.informatik.utils.threaded.disruptor.DisruptorProvider;

/**
 * Abstract event handler that is used by a {@link DisruptorProvider}. Uses
 * a "first come, first serve" (FCFS) strategy to assign a handler to a single event.
 * Any published event may be processed by any idle handler.
 * 
 * @author Simon Heiden
 * @param <A>
 * the type of elements that shall be processed by this handler
 * @see DisruptorProvider
 */
public abstract class DisruptorFCFSEventHandler<A> extends AbstractDisruptorEventHandler<A> {

    /**
     * Creates a {@link DisruptorFCFSEventHandler}.
     */
    public DisruptorFCFSEventHandler() {
        this(false);
    }
    
    /**
     * Creates a {@link DisruptorFCFSEventHandler}.
     * @param isSingle
     * whether this consumer is the only one which is
     * connected to the disruptor
     */
    public DisruptorFCFSEventHandler(boolean isSingle) {
    	super(isSingle);
    }
    
   
    
	@Override
	public void onEvent(SingleUseEvent<A> event, long sequence, boolean endOfBatch) throws Exception {
		if (isSingleConsumer() || event.isFirstAccess()) {
			super.onEvent(event, sequence, endOfBatch);
        }
	}
	
}
