/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.pipes;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import se.de.hu_berlin.informatik.utils.threaded.CallableWithPaths;
import se.de.hu_berlin.informatik.utils.tm.modules.ThreadedElementProcessorModule;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.APipe;

/**
 * Starts a threaded element processor with a provided callable class on each
 * submitted input element.
 * 
 * @author Simon Heiden
 * 
 * @see ThreadedElementProcessorModule
 * @see CallableWithPaths
 * @see Callable
 */
public class ThreadedProcessorPipe<A> extends APipe<A,Boolean> {

	private boolean executorGiven = false;
	private ThreadedElementProcessorModule<A> processor;

	/**
	 * Creates a new {@link ThreadedProcessorPipe} object with the given parameters.
	 * @param threadCount
	 * the number of threads that shall be run in parallel
	 * @param clazz
	 * a {@link CallableWithPaths} class which is called for every matching file
	 * @param clazzConstructorArguments
	 * arguments that might be needed in the constructor of the callable class
	 */
	public ThreadedProcessorPipe(int threadCount,
			Class<? extends CallableWithPaths<A,?>> clazz, Object... clazzConstructorArguments) {
		super();
		processor = new ThreadedElementProcessorModule<A>(threadCount, clazz, clazzConstructorArguments);
	}
	
	/**
	 * Creates a new {@link ThreadedProcessorPipe} object with the given parameters. 
	 * @param executor
	 * an executor service that shall be used
	 * @param clazz
	 * a {@link CallableWithPaths} class which is called for every matching file
	 * @param clazzConstructorArguments
	 * arguments that might be needed in the constructor of the callable class
	 */
	public ThreadedProcessorPipe(ExecutorService executor,
			Class<? extends CallableWithPaths<A,?>> clazz, Object... clazzConstructorArguments) {
		super();
		processor = new ThreadedElementProcessorModule<A>(executor, clazz, clazzConstructorArguments);
		executorGiven = true;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public Boolean processItem(A input) {
		processor.submit(input);
		
		return null;
	}

	@Override
	public boolean finalShutdown() {
		if (!executorGiven) {
			return processor.getExecutorServiceProvider().shutdownAndWaitForTermination();
		}
		return true;
	}
	
	

}
