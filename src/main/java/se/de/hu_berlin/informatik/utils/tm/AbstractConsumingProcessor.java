package se.de.hu_berlin.informatik.utils.tm;

import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.DisruptorFCFSEventHandler;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AbstractModule;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipe;

public abstract class AbstractConsumingProcessor<A> implements ConsumingProcessor<A> {

	private AbstractPipe<A, ?> pipeView;
	private AbstractModule<A, ?> moduleView;
	private DisruptorFCFSEventHandler<A> ehView;

	@Override
	public AbstractPipe<A, ?> asPipe() throws UnsupportedOperationException {
		if (pipeView == null) {
			pipeView = newPipeInstance();
		}
		return pipeView;
	}

	@Override
	public AbstractModule<A, ?> asModule() throws UnsupportedOperationException {
		if (moduleView == null) {
			moduleView = newModuleInstance();
		}
		return moduleView;
	}

	@Override
	public DisruptorFCFSEventHandler<A> asEH() throws UnsupportedOperationException {
		if (ehView == null) {
			ehView = newEHInstance();
		}
		return ehView;
	}

}
