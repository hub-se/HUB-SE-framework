package se.de.hu_berlin.informatik.utils.threaded;

public abstract class ADisruptorEventHandlerWMultiplexerFactory<A,B> extends ADisruptorEventHandlerFactory<A> {

	private IMultiplexer<B> multiplexer;
	
	/**
	 * @param eventHandlerClass
	 * the class type of returned instances 
	 */
	public ADisruptorEventHandlerWMultiplexerFactory(Class<? extends DisruptorFCFSEventHandler<A>> eventHandlerClass) {
		super(eventHandlerClass);
	}

	@Override
	public DisruptorFCFSEventHandler<A> newInstance() {
		CallableWithInputAndReturn<A,B> call = getNewInstance();
		call.setMultiplexer(multiplexer);
		return call;
	}

	public abstract CallableWithInputAndReturn<A,B> getNewInstance();

	public void setMultiplexer(IMultiplexer<B> multiplexer) {
		this.multiplexer = multiplexer;
	}
	
}
