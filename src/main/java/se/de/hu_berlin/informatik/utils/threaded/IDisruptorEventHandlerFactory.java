package se.de.hu_berlin.informatik.utils.threaded;

public interface IDisruptorEventHandlerFactory<A> {

	public Class<? extends DisruptorEventHandler<A>> getEventHandlerClass();
	
	public DisruptorEventHandler<A> newInstance();
	
}
