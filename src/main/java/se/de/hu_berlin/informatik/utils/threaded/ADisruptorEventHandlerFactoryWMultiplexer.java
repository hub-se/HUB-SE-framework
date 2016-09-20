package se.de.hu_berlin.informatik.utils.threaded;

public abstract class ADisruptorEventHandlerFactoryWMultiplexer<A,B> extends ADisruptorEventHandlerFactory<A> {

	private IMultiplexer<B> multiplexer;
	
	/**
	 * @param eventHandlerClass
	 * the class type of returned instances 
	 */
	public ADisruptorEventHandlerFactoryWMultiplexer(Class<? extends DisruptorEventHandler<A>> eventHandlerClass) {
		super(eventHandlerClass);
	}

	@Override
	public DisruptorEventHandler<A> newInstance() {
		CallableWithInputAndReturn<A,B> call = getNewInstance();
		call.setMultiplexer(multiplexer);
		return call;
	}

	public abstract CallableWithInputAndReturn<A,B> getNewInstance();

	public void setMultiplexer(IMultiplexer<B> multiplexer) {
		this.multiplexer = multiplexer;
	}
	
}
