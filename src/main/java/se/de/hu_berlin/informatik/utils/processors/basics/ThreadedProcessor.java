/**
 * 
 */
package se.de.hu_berlin.informatik.utils.processors.basics;

import se.de.hu_berlin.informatik.utils.optionparser.OptionParser;
import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;
import se.de.hu_berlin.informatik.utils.processors.BasicComponent;
import se.de.hu_berlin.informatik.utils.processors.sockets.ProcessorSocket;
import se.de.hu_berlin.informatik.utils.processors.sockets.ProcessorSocketGenerator;
import se.de.hu_berlin.informatik.utils.threaded.ThreadLimit;
import se.de.hu_berlin.informatik.utils.threaded.ThreadLimitDummy;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.AbstractDisruptorMultiplexer;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.DisruptorProvider;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.AbstractDisruptorEventHandler;

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
	private ProcessorSocket<A,B> socket;

	private ThreadedProcessor(ClassLoader classLoader) {
		super();
		disruptorProvider = new DisruptorProvider<>(1024, classLoader);
		//starts a multiplexer with the created disruptor
		multiplexer = new AbstractDisruptorMultiplexer<B>(disruptorProvider) {
			@Override
			public void processNewOutputItem(B item) {
				//submit results that are not null to the ouput pipe
				socket.produce(item);
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
	
	
	@Override
	public B processItem(A input, ProcessorSocket<A, B> socket) {
		this.socket = socket;
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

	@Override
	public BasicComponent setOptions(OptionParser options) {
		super.setOptions(options);
		for (AbstractDisruptorEventHandler<A> handler : disruptorProvider.getHandlers()) {
			handler.setOptions(options);
		}
		return this;
	}
	
}
