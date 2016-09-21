package se.de.hu_berlin.informatik.utils.threaded;

public abstract class ADisruptorEventHandlerFactory<A> implements IDisruptorEventHandlerFactory<A> {

	Class<? extends DisruptorFCFSEventHandler<A>> eventHandlerClass;
			
	public ADisruptorEventHandlerFactory(Class<? extends DisruptorFCFSEventHandler<A>> eventHandlerClass) {
		this.eventHandlerClass = eventHandlerClass;
	}
	
	@Override
	public Class<? extends DisruptorFCFSEventHandler<A>> getEventHandlerClass() {
		return eventHandlerClass;
	}
	
}
