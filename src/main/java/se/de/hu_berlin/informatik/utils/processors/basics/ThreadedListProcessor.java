/**
 * 
 */
package se.de.hu_berlin.informatik.utils.processors.basics;

import java.util.List;

import se.de.hu_berlin.informatik.utils.processors.AbstractConsumingProcessor;
import se.de.hu_berlin.informatik.utils.processors.sockets.ConsumingProcessorSocket;
import se.de.hu_berlin.informatik.utils.processors.sockets.ConsumingProcessorSocketGenerator;
import se.de.hu_berlin.informatik.utils.processors.sockets.ProcessorSocket;
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
	ProcessorSocket<List<A>, Object> socket;

	public ThreadedListProcessor(Integer threadCount, ConsumingProcessorSocketGenerator<A> callableFactory, ClassLoader cl) {
		super();
		disruptorProvider = new DisruptorProvider<>(cl);
		disruptorProvider.connectHandlers(callableFactory, threadCount);
	}
	
	public ThreadedListProcessor(Integer threadCount, ConsumingProcessorSocketGenerator<A> callableFactory) {
		this(threadCount, callableFactory, null);
	}

	@Override
	public void consumeItem(List<A> input, ConsumingProcessorSocket<List<A>> socket) {
		if (this.socket == null) {
			this.socket = socket;
			if (this.socket.hasOptions()) {
				for (AbstractDisruptorEventHandler<A> handler : disruptorProvider.getHandlers()) {
					handler.setOptions(this.socket.getOptions());
				}
			}
		}
		for (A element : input) {
			disruptorProvider.submit(element);
		}
		disruptorProvider.shutdown();
	}
	
}
