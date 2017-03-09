/**
 * 
 */
package se.de.hu_berlin.informatik.utils.processors.basics;

import java.util.List;

import se.de.hu_berlin.informatik.utils.processors.AbstractConsumingProcessor;
import se.de.hu_berlin.informatik.utils.processors.sockets.ConsumingProcessorSocketGenerator;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.DisruptorProvider;

/**
 * Starts a threaded element processor with a provided callable class on each
 * element in a given input list.
 * 
 * @author Simon Heiden
 */
public class ThreadedListProcessor<A> extends AbstractConsumingProcessor<List<A>> {

	private DisruptorProvider<A> disruptorProvider;

	public ThreadedListProcessor(Integer threadCount, ConsumingProcessorSocketGenerator<A> callableFactory) {
		super();
		disruptorProvider = new DisruptorProvider<>();
		disruptorProvider.connectHandlers(callableFactory, threadCount);
	}

	public void consume(List<A> input) {
		for (A element : input) {
			disruptorProvider.submit(element);
		}
		disruptorProvider.shutdown();
	}

}
