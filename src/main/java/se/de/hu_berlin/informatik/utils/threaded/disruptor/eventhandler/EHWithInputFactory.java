package se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler;

public abstract class EHWithInputFactory<A> extends AbstractDisruptorEventHandlerFactory<A> {

	/**
	 * @param eventHandlerClass
	 * the class type of returned instances 
	 */
	public EHWithInputFactory(Class<? extends EHWithInput<A>> eventHandlerClass) {
		super(eventHandlerClass);
	}
	
	public EHWithInputFactory() {
		super();
	}

	@Override
	public EHWithInput<A> newInstance() {
		EHWithInput<A> call = newFreshInstance();
		call.setThreadLimit(getThreadLimit());
		return call;
	}

	@Override
	public abstract EHWithInput<A> newFreshInstance();
	
}
