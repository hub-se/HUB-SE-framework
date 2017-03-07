package se.de.hu_berlin.informatik.utils.tm;

import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInputAndReturn;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AbstractModule;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipe;

public abstract class AbstractProcessor<A,B> extends AbstractComponent implements Processor<A,B> { 

	private AbstractPipe<A,B> pipeView;
	private AbstractModule<A,B> moduleView;
	private EHWithInputAndReturn<A,B> ehView;
	private Producer<B> producer;

	public AbstractModule<A,B> submit(Object item) {
		return asModule().submit(item);
	}
	
	@Override
	public AbstractPipe<A,B> asPipe() throws UnsupportedOperationException {
		if (pipeView == null) {
			pipeView = new AbstractPipe<>(this, true);
		}
		return pipeView;
	}

	@Override
	public AbstractModule<A,B> asModule() throws UnsupportedOperationException {
		if (moduleView == null) {
			moduleView = new AbstractModule<>(this);
		}
		return moduleView;
	}

	@Override
	public EHWithInputAndReturn<A,B> asEH() throws UnsupportedOperationException {
		if (ehView == null) {
			ehView = new EHWithInputAndReturn<>(this);
		}
		return ehView;
	}

	@Override
	public void setProducer(Producer<B> producer) {
		if (producer == null) {
			throw new IllegalStateException("No producer given (null) for " + this.getClass().getSimpleName() + ".");
		}
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
