/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.pipes;

import se.de.hu_berlin.informatik.utils.threaded.ADisruptorEventHandlerFactoryWCallback;
import se.de.hu_berlin.informatik.utils.threaded.DisruptorProvider;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.APipe;

/**
 * Starts a provided callable class on each submitted input element, using
 * a specifiec number of threads in parallel.
 * 
 * @author Simon Heiden
 */
public class ThreadedProcessorPipe<A,B> extends APipe<A,B> {

	private DisruptorProvider<A> disruptorProvider;

	public ThreadedProcessorPipe(int threadCount, ADisruptorEventHandlerFactoryWCallback<A,B> callableFactory) {
		super(false);
		disruptorProvider = new DisruptorProvider<>(8);
		callableFactory.setCallbackPipe(this);
		disruptorProvider.connectHandlers(threadCount, callableFactory);
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public B processItem(A input) {
		disruptorProvider.submit(input);
		return null;
	}

	@Override
	public boolean finalShutdown() {
		disruptorProvider.shutdown();
		return true;
	}

}
