/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.pipes;

import se.de.hu_berlin.informatik.utils.threaded.ADisruptorEventHandlerFactoryWCallback;
import se.de.hu_berlin.informatik.utils.threaded.AMultiplexer;
import se.de.hu_berlin.informatik.utils.threaded.DisruptorProvider;
import se.de.hu_berlin.informatik.utils.threaded.IMultiplexer;
import se.de.hu_berlin.informatik.utils.threaded.IMultiplexerInput;
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

	@SuppressWarnings("unchecked")
	public ThreadedProcessorPipe(int threadCount, ADisruptorEventHandlerFactoryWCallback<A,B> callableFactory) {
		super(true);
		disruptorProvider = new DisruptorProvider<>(8);
		//starts a multiplexer without any connected handlers (the handlers must have
		//a reference to the multiplexer to wake him up if output was generated)
		multiplexer = new AMultiplexer<B>() {
			@Override
			public void processNewOutputItem(B item) {
				//submit results that are not null to the ouput pipe
				submitProcessedItem(item);
			}
		};
		//propagate the reference to the multiplexer to the handlers to be created
		callableFactory.setMultiplexer(multiplexer);
		//connect the handlers to the disruptor
		disruptorProvider.connectHandlers(threadCount, callableFactory);
		
		//now that the handlers are instantiated, we can connect them to the multiplexer
		multiplexer.connectHandlers((IMultiplexerInput<B>[]) disruptorProvider.getHandlers());
		//...and we can start the multiplexer thread (which will park itself until notified
		//of any generated output
		multiplexer.start();
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
		//after shutting down the disruptor, we have to shut down the multiplexer, too
		multiplexer.shutdown();
		return true;
	}

}
