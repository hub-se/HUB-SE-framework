package se.de.hu_berlin.informatik.utils.threaded;

import com.lmax.disruptor.EventHandler;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;

public abstract class DisruptorEventHandler<A> implements EventHandler<Event<A>> {

	private long ordinal;
    private long numberOfConsumers;
    
    private DisruptorProvider<A> callback = null;
    
    private boolean single = false;

    public DisruptorEventHandler() {
        this(-1,-1);
    }
    
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
    
    public void setIndex(long ordinal) {
    	this.ordinal = ordinal;
    }
    
    public void setCallback(DisruptorProvider<A> callback) {
    	this.callback = callback;
    }
    
    public void setNumberOfConsumers(long numberOfConsumers) {
    	this.numberOfConsumers = numberOfConsumers;
    	if (numberOfConsumers == 1) {
    		single = true;
    	} else {
    		single = false;
    	}
    }
    
	@Override
	public void onEvent(Event<A> event, long sequence, boolean endOfBatch) throws Exception {
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
	
	abstract public void processEvent(A input) throws Exception;
	
}
