package se.de.hu_berlin.informatik.utils.threaded;

public interface IDisruptorEventHandlerFactoryWCallback<A,B> extends IDisruptorEventHandlerFactory<A> {

	public void setMultiplexer(IMultiplexer<B> multiplexer);
	
}
