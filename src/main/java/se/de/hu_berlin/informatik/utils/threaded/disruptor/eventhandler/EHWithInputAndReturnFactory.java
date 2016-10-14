package se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler;

import se.de.hu_berlin.informatik.utils.threaded.disruptor.Multiplexer;

public abstract class EHWithInputAndReturnFactory<A,B> extends AbstractDisruptorEventHandlerFactory<A> {

	private Multiplexer<B> multiplexer;
	
	/**
	 * @param eventHandlerClass
	 * the class type of returned instances 
	 */
	public EHWithInputAndReturnFactory(Class<? extends EHWithInputAndReturn<A,B>> eventHandlerClass) {
		super(eventHandlerClass);
	}
	
	public EHWithInputAndReturnFactory() {
		super();
	}

	@Override
	public EHWithInputAndReturn<A,B> newInstance() {
		EHWithInputAndReturn<A,B> call = newFreshInstance();
		call.setMultiplexer(multiplexer);
		call.setThreadLimit(getThreadLimit());
		return call;
	}

	@Override
	public abstract EHWithInputAndReturn<A,B> newFreshInstance();

	public void setMultiplexer(Multiplexer<B> multiplexer) {
		this.multiplexer = multiplexer;
	}
	
}
