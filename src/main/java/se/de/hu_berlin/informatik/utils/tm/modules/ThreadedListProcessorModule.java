/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.modules;

import java.util.List;

import se.de.hu_berlin.informatik.utils.threaded.disruptor.DisruptorProvider;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AbstractModule;
import se.de.hu_berlin.informatik.utils.tm.user.ConsumingProcessorUser;

/**
 * Starts a threaded element processor with a provided callable class on each
 * element in a given input list.
 * 
 * @author Simon Heiden
 */
public class ThreadedListProcessorModule<A> extends AbstractModule<List<A>,Boolean> {

	private DisruptorProvider<A> disruptorProvider;

	public ThreadedListProcessorModule(Integer threadCount, ConsumingProcessorUser<A> callableFactory) {
		super(true);
		disruptorProvider = new DisruptorProvider<>();
		disruptorProvider.connectHandlers(callableFactory, threadCount);
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public Boolean processItem(List<A> input) {
		delegateTrackingTo(disruptorProvider);
		
		for (A element : input) {
			disruptorProvider.submit(element);
		}
		disruptorProvider.shutdown();
		
		disruptorProvider.delegateTrackingTo(this);
		return null;
	}

}
