package se.de.hu_berlin.informatik.utils.tm;

import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInputAndReturn;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AbstractModule;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipe;

public abstract class AbstractProcessor<A,B> implements Processor<A,B> { 

	private AbstractPipe<A,B> pipeView;
	private AbstractModule<A,B> moduleView;
	private EHWithInputAndReturn<A,B> ehView;
	private Producer<B> producer;

	@Override
	public AbstractPipe<A,B> asPipe() throws UnsupportedOperationException {
		if (pipeView == null) {
			pipeView = newPipeInstance();
		}
		return pipeView;
	}

	@Override
	public AbstractModule<A,B> asModule() throws UnsupportedOperationException {
		if (moduleView == null) {
			moduleView = newModuleInstance();
		}
		return moduleView;
	}

	@Override
	public EHWithInputAndReturn<A,B> asEH() throws UnsupportedOperationException {
		if (ehView == null) {
			ehView = newEHInstance();
		}
		return ehView;
	}

	@Override
	public void setProducer(Producer<B> producer) {
		this.producer = producer;
	}

	@Override
	public Producer<B> getProducer() {
		if (producer == null) {
			throw new IllegalStateException("No producer set for " + this.getClass().getSimpleName() + ".");
		} else {
			return producer;
		}
	}

}
