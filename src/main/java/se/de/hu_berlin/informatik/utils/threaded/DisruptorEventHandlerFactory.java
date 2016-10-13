package se.de.hu_berlin.informatik.utils.threaded;

public interface DisruptorEventHandlerFactory<A> {

	/**
	 * @return
	 * the actual class of the event handler; needed for instantiation of an array;
	 * in the default implementation, this creates a new instance of an event handler
	 * with the given methods and returns its class. May be overridden if the
	 * creation of an event handler is very time consuming or may cause errors.
	 */
	@SuppressWarnings("unchecked")
	default public Class<? extends AbstractDisruptorEventHandler<A>> getEventHandlerClass() {
		return (Class<? extends AbstractDisruptorEventHandler<A>>) newFreshInstance().getClass();
	}
	
	/**
	 * Should take a fresh instance and perform necessary
	 * modifications to initialize it correctly, if needed.
	 * @return
	 * a completely finished, usable instance of an event handler
	 */
	public AbstractDisruptorEventHandler<A> newInstance();
	
	/**
	 * Should return an instance of an event handler which
	 * may not quite be usable without some modifications
	 * or further initialization.
	 * @return
	 * a new instance of an event handler
	 */
	public AbstractDisruptorEventHandler<A> newFreshInstance();
	
	/**
	 * @return
	 * an object that limits the amount of threads that
	 * run in parallel.
	 */
	public ThreadLimit getThreadLimit();
	
	/** 
	 * @param limit
	 * an object that limits the amount of threads that
	 * run in parallel. 
	 */
	public void setThreadLimit(ThreadLimit limit);
	
}
