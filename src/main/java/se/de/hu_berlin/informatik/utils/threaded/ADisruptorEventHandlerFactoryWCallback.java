package se.de.hu_berlin.informatik.utils.threaded;

import se.de.hu_berlin.informatik.utils.tm.pipeframework.APipe;

public abstract class ADisruptorEventHandlerFactoryWCallback<A,B> implements IDisruptorEventHandlerFactoryWCallback<A,B> {

	private APipe<?, B> pipe;

	@Override
	public DisruptorEventHandler<A> newInstance() {
		CallableWithReturn<A,B> call = getNewInstance();
		call.setPipe(pipe);
		return call;
	}

	public abstract CallableWithReturn<A,B> getNewInstance();

	@Override
	public void setCallbackPipe(APipe<?, B> pipe) {
		this.pipe = pipe;
	}
		
}
