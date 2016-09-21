/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.pipes;

import se.de.hu_berlin.informatik.utils.threaded.ADisruptorEventHandlerWMultiplexerFactory;
import se.de.hu_berlin.informatik.utils.threaded.ADisruptorMultiplexer;
import se.de.hu_berlin.informatik.utils.threaded.DisruptorProvider;
import se.de.hu_berlin.informatik.utils.threaded.IMultiplexer;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.APipe;

/**
 * Starts a provided callable class on each submitted input element, using
 * a specifiec number of threads in parallel. The output of the threads
 * is collected with a multiplexer thread which returns it to the linked 
 * output pipe. {@code null} objects are ignored by the multiplexer.
 * 
 * @author Simon Heiden
 */
public class ThreadedProcessorPipe<A,B> extends APipe<A,B> {

	private DisruptorProvider<A> disruptorProvider;
	private IMultiplexer<B> multiplexer;

	public ThreadedProcessorPipe(int threadCount, ADisruptorEventHandlerWMultiplexerFactory<A,B> callableFactory) {
		super(true);
		disruptorProvider = new DisruptorProvider<>(8);
		//starts a multiplexer with the created disruptor
		multiplexer = new ADisruptorMultiplexer<B>(disruptorProvider) {
			@Override
			public void processNewOutputItem(B item) {
				//submit results that are not null to the ouput pipe
				submitProcessedItem(item);
			}
		};
		//we have to propagate the reference to the multiplexer to the handlers to be created
		//(the handlers must have a reference to the multiplexer to wake him up if output was generated)
		callableFactory.setMultiplexer(multiplexer);
		//connect the handlers to the disruptor
		disruptorProvider.connectHandlers(threadCount, callableFactory);
		
		//now that the handlers are instantiated, we can connect them to the multiplexer
		//by starting the multiplexer thread (which will park itself until notified
		//of any generated output
		multiplexer.start();
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public B processItem(A input) {
		//restart the multiplexer if it has been shut down
		if (!multiplexer.isRunning()) {
			multiplexer.start();
		}
		disruptorProvider.submit(input);
		return null;
	}

	@Override
	public boolean finalShutdown() {
		disruptorProvider.shutdown();
		//after shutting down the disruptor, we have to shut down the multiplexer, too
		multiplexer.shutdown();
		return true;
	}

}
