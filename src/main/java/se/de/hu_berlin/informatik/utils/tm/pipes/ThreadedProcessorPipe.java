/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.pipes;

import se.de.hu_berlin.informatik.utils.threaded.DisruptorProvider;
import se.de.hu_berlin.informatik.utils.threaded.IDisruptorEventHandlerFactory;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.APipe;

/**
 * Starts a threaded element processor with a provided callable class on each
 * submitted input element.
 * 
 * @author Simon Heiden
 */
public class ThreadedProcessorPipe<A> extends APipe<A,Boolean> {

	private DisruptorProvider<A> disruptorProvider;

	public ThreadedProcessorPipe(int threadCount, IDisruptorEventHandlerFactory<A> callableFactory) {
		super(false);
		disruptorProvider = new DisruptorProvider<>(8);
		disruptorProvider.connectHandlers(threadCount, callableFactory);
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public Boolean processItem(A input) {
		disruptorProvider.submit(input);
		return null;
	}

	@Override
	public boolean finalShutdown() {
		disruptorProvider.shutdown();
		return true;
	}

}
