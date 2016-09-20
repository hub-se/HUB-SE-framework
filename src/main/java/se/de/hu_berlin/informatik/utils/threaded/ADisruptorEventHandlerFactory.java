package se.de.hu_berlin.informatik.utils.threaded;

public abstract class ADisruptorEventHandlerFactory<A> implements IDisruptorEventHandlerFactory<A> {

	Class<? extends DisruptorEventHandler<A>> eventHandlerClass;
			
	public ADisruptorEventHandlerFactory(Class<? extends DisruptorEventHandler<A>> eventHandlerClass) {
		this.eventHandlerClass = eventHandlerClass;
	}
	
	@Override
	public Class<? extends DisruptorEventHandler<A>> getEventHandlerClass() {
		return eventHandlerClass;
	}
	
}
