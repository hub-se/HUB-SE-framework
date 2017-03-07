/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.modules;

import java.util.List;

import se.de.hu_berlin.informatik.utils.threaded.disruptor.DisruptorProvider;
import se.de.hu_berlin.informatik.utils.tm.AbstractConsumingProcessor;
import se.de.hu_berlin.informatik.utils.tm.user.ConsumingProcessorUserGenerator;

/**
 * Starts a threaded element processor with a provided callable class on each
 * element in a given input list.
 * 
 * @author Simon Heiden
 */
public class ThreadedListProcessorModule<A> extends AbstractConsumingProcessor<List<A>> {

	private DisruptorProvider<A> disruptorProvider;

	public ThreadedListProcessorModule(Integer threadCount, ConsumingProcessorUserGenerator<A> callableFactory) {
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
