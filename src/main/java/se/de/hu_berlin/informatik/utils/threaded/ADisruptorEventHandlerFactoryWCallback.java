package se.de.hu_berlin.informatik.utils.threaded;

public abstract class ADisruptorEventHandlerFactoryWCallback<A,B> implements IDisruptorEventHandlerFactoryWCallback<A,B> {

	private IMultiplexer<B> multiplexer;

	@Override
	public DisruptorEventHandler<A> newInstance() {
		CallableWithReturn<A,B> call = getNewInstance();
		call.setMultiplexer(multiplexer);
		return call;
	}

	public abstract CallableWithReturn<A,B> getNewInstance();

	@Override
	public void setMultiplexer(IMultiplexer<B> multiplexer) {
		this.multiplexer = multiplexer;
	}
	
}
