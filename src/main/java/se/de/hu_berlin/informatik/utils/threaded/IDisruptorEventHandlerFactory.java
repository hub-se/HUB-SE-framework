package se.de.hu_berlin.informatik.utils.threaded;

public interface IDisruptorEventHandlerFactory<A> {

	public Class<? extends DisruptorFCFSEventHandler<A>> getEventHandlerClass();
	
	public DisruptorFCFSEventHandler<A> newInstance();
	
}
