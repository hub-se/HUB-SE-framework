/**
 * 
 */
package se.de.hu_berlin.informatik.utils.processors.basics;

import java.util.List;

import se.de.hu_berlin.informatik.utils.optionparser.OptionParser;
import se.de.hu_berlin.informatik.utils.processors.AbstractConsumingProcessor;
import se.de.hu_berlin.informatik.utils.processors.BasicComponent;
import se.de.hu_berlin.informatik.utils.processors.sockets.ConsumingProcessorSocketGenerator;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.DisruptorProvider;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.AbstractDisruptorEventHandler;

/**
 * Starts a threaded element processor with a provided callable class on each
 * element in a given input list.
 * 
 * @author Simon Heiden
 */
public class ThreadedListProcessor<A> extends AbstractConsumingProcessor<List<A>> {

	private DisruptorProvider<A> disruptorProvider;

	public ThreadedListProcessor(Integer threadCount, ConsumingProcessorSocketGenerator<A> callableFactory, ClassLoader cl) {
		super();
		disruptorProvider = new DisruptorProvider<>(cl);
		disruptorProvider.connectHandlers(callableFactory, threadCount);
	}
	
	public ThreadedListProcessor(Integer threadCount, ConsumingProcessorSocketGenerator<A> callableFactory) {
		this(threadCount, callableFactory, null);
	}

	@Override
	public void consumeItem(List<A> input) {
		for (A element : input) {
			disruptorProvider.submit(element);
		}
		disruptorProvider.shutdown();
	}

	@Override
	public BasicComponent setOptions(OptionParser options) {
		super.setOptions(options);
		for (AbstractDisruptorEventHandler<A> handler : disruptorProvider.getHandlers()) {
			handler.setOptions(options);
		}
		return this;
	}
	
}
