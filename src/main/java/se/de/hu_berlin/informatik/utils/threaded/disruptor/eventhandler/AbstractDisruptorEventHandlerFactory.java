package se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler;

import se.de.hu_berlin.informatik.utils.threaded.ThreadLimit;
import se.de.hu_berlin.informatik.utils.threaded.ThreadLimitDummy;

public abstract class AbstractDisruptorEventHandlerFactory<A> implements DisruptorEventHandlerFactory<A> {

	private Class<? extends AbstractDisruptorEventHandler<A>> eventHandlerClass = null;
	private ThreadLimit limit = ThreadLimitDummy.getInstance();
			
	/**
	 * @param eventHandlerClass
	 * the actual class of the event handlers that are being produced
	 */
	public AbstractDisruptorEventHandlerFactory(Class<? extends AbstractDisruptorEventHandler<A>> eventHandlerClass) {
		this();
		this.eventHandlerClass = eventHandlerClass;
	}
	
	public AbstractDisruptorEventHandlerFactory() {
		super();
	}
	
	@Override
	public AbstractDisruptorEventHandler<A> newInstance() {
		AbstractDisruptorEventHandler<A> call = newFreshInstance();
		call.setThreadLimit(getThreadLimit());
		return call;
	}

	@Override
	public abstract AbstractDisruptorEventHandler<A> newFreshInstance();
	
	@Override
	public Class<? extends AbstractDisruptorEventHandler<A>> getEventHandlerClass() {
		if (eventHandlerClass == null) {
			return DisruptorEventHandlerFactory.super.getEventHandlerClass();
		}
		return eventHandlerClass;
	}

	@Override
	public ThreadLimit getThreadLimit() {
		return limit;
	}

	@Override
	public void setThreadLimit(ThreadLimit limit) {
		this.limit = limit;
	}

}
