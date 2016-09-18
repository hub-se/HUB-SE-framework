package se.de.hu_berlin.informatik.utils.threaded;

import se.de.hu_berlin.informatik.utils.tm.pipeframework.APipe;

public interface IDisruptorEventHandlerFactoryWCallback<A,B> extends IDisruptorEventHandlerFactory<A> {

	public void setCallbackPipe(APipe<?, B> pipe);
	
}
