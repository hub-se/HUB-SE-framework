/**
 * 
 */
package se.de.hu_berlin.informatik.utils.processors.basics;

import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;
import se.de.hu_berlin.informatik.utils.processors.Producer;
import se.de.hu_berlin.informatik.utils.processors.sockets.ProcessorSocketGenerator;
import se.de.hu_berlin.informatik.utils.threaded.ThreadLimit;
import se.de.hu_berlin.informatik.utils.threaded.ThreadLimitDummy;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.AbstractDisruptorMultiplexer;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.DisruptorProvider;

/**
 * Starts a provided callable class on each submitted input element, using
 * a specifiec number of threads in parallel. The output of the threads
 * is collected with a multiplexer thread which returns it to the linked 
 * output pipe. {@code null} objects are ignored by the multiplexer.
 * 
 * @author Simon Heiden
 */
public class ThreadedProcessor<A,B> extends AbstractProcessor<A,B> {

	private DisruptorProvider<A> disruptorProvider;
	private AbstractDisruptorMultiplexer<B> multiplexer;
	private Producer<B> producer;

	private ThreadedProcessor(ClassLoader classLoader) {
		super();
		disruptorProvider = new DisruptorProvider<>(1024, classLoader);
		//starts a multiplexer with the created disruptor
		multiplexer = new AbstractDisruptorMultiplexer<B>(disruptorProvider) {
			@Override
			public void processNewOutputItem(B item) {
				//submit results that are not null to the ouput pipe
				producer.produce(item);
			}
		};
	}
	
	public ThreadedProcessor(int threadCount, ThreadLimit limit, ProcessorSocketGenerator<A,B> transmitter, ClassLoader classLoader) {
		this(classLoader);
		//connect the handlers to the disruptor
		disruptorProvider.connectHandlers(transmitter, threadCount, limit, multiplexer);
		
		initMultiplexer();
	}
	
	public ThreadedProcessor(int threadCount, ThreadLimit limit, ProcessorSocketGenerator<A,B> transmitter) {
		this(threadCount, limit, transmitter, null);
	}

	public ThreadedProcessor(int threadCount, ProcessorSocketGenerator<A,B> transmitter, ClassLoader classLoader) {
		this(threadCount, ThreadLimitDummy.getInstance(), transmitter, classLoader);
	}
	
	public ThreadedProcessor(int threadCount, ProcessorSocketGenerator<A,B> transmitter) {
		this(threadCount, ThreadLimitDummy.getInstance(), transmitter, null);
	}
	
	private void initMultiplexer() {
		//now that the handlers are instantiated, we can connect them to the multiplexer
		//by starting the multiplexer thread (which will park itself until notified
		//of any generated output
		multiplexer.startAndConnectHandlers();
	}
	
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public B processItem(A input, Producer<B> producer) {
		this.producer = producer;
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
