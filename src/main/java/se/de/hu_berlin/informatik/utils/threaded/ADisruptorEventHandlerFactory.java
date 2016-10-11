package se.de.hu_berlin.informatik.utils.threaded;

public abstract class ADisruptorEventHandlerFactory<A> implements IDisruptorEventHandlerFactory<A> {

	private Class<? extends ADisruptorEventHandler<A>> eventHandlerClass = null;
	private IThreadLimit limit = ThreadLimitDummy.getInstance();
			
	/**
	 * @param eventHandlerClass
	 * the actual class of the event handlers that are being produced
	 */
	public ADisruptorEventHandlerFactory(Class<? extends ADisruptorEventHandler<A>> eventHandlerClass) {
		this();
		this.eventHandlerClass = eventHandlerClass;
	}
	
	public ADisruptorEventHandlerFactory() {
		super();
	}
	
	@Override
	public ADisruptorEventHandler<A> newInstance() {
		ADisruptorEventHandler<A> call = newFreshInstance();
		call.setThreadLimit(getThreadLimit());
		return call;
	}

	@Override
	public abstract ADisruptorEventHandler<A> newFreshInstance();
	
	@Override
	public Class<? extends ADisruptorEventHandler<A>> getEventHandlerClass() {
		if (eventHandlerClass == null) {
			return IDisruptorEventHandlerFactory.super.getEventHandlerClass();
		}
		return eventHandlerClass;
	}

	@Override
	public IThreadLimit getThreadLimit() {
		return limit;
	}

	@Override
	public void setThreadLimit(IThreadLimit limit) {
		this.limit = limit;
	}

}
