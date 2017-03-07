package se.de.hu_berlin.informatik.utils.tm;

import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.DisruptorFCFSEventHandler;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInputAndReturn;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.Module;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.Pipe;
import se.de.hu_berlin.informatik.utils.tm.user.ProcessorUser;

public abstract class AbstractProcessor<A,B> extends AbstractComponent implements Processor<A,B> { 

	private Pipe<A,B> pipeView;
	private Module<A,B> moduleView;
	private EHWithInputAndReturn<A,B> ehView;
	private Producer<B> producer;

	public Module<A,B> submit(Object item) {
		return asModule().submit(item);
	}
	
	@Override
	public Pipe<A,B> asPipe() throws UnsupportedOperationException {
		if (pipeView == null) {
			pipeView = new Pipe<>(this, true);
		}
		return pipeView;
	}

	@Override
	public Module<A,B> asModule() throws UnsupportedOperationException {
		if (moduleView == null) {
			moduleView = new Module<>(this);
		}
		return moduleView;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E extends DisruptorFCFSEventHandler<A> & ProcessorUser<A,B>> E asEH() throws UnsupportedOperationException {
		if (ehView == null) {
			ehView = new EHWithInputAndReturn<>(this);
		}
		return (E) ehView;
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
