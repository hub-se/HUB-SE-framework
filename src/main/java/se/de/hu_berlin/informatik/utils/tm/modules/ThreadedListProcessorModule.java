/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.modules;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import se.de.hu_berlin.informatik.utils.threaded.CallableWithPaths;
import se.de.hu_berlin.informatik.utils.threaded.ExecutorServiceProvider;
import se.de.hu_berlin.informatik.utils.threaded.ThreadedElementProcessor;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AModule;

/**
 * Starts a threaded element processor with a provided callable class on each
 * element in a given input list.
 * 
 * @author Simon Heiden
 * 
 * @see ThreadedElementProcessor
 * @see CallableWithPaths
 * @see Callable
 */
public class ThreadedListProcessorModule<A> extends AModule<List<A>,Boolean> {

	private int threadCount;
	private ExecutorServiceProvider executor = null;
	private boolean executorGiven = false;
	private Class<? extends CallableWithPaths<A,?>> clazz;
	private Object[] clazzConstructorArguments;

	/**
	 * Creates a new {@link ThreadedListProcessorModule} object with the given parameters.
	 * @param threadCount
	 * the number of threads that shall be run in parallel
	 * @param clazz
	 * a {@link CallableWithPaths} class which is called for every matching file
	 * @param clazzConstructorArguments
	 * arguments that might be needed in the constructor of the callable class
	 */
	public ThreadedListProcessorModule(int threadCount,
			Class<? extends CallableWithPaths<A,?>> clazz, Object... clazzConstructorArguments) {
		super(true);
		this.threadCount = threadCount;
		this.clazz = clazz;
		this.clazzConstructorArguments = clazzConstructorArguments;
	}
	
	/**
	 * Creates a new {@link ThreadedListProcessorModule} object with the given parameters. 
	 * @param executor
	 * an executor service that shall be used
	 * @param clazz
	 * a {@link CallableWithPaths} class which is called for every matching file
	 * @param clazzConstructorArguments
	 * arguments that might be needed in the constructor of the callable class
	 */
	public ThreadedListProcessorModule(ExecutorService executor,
			Class<? extends CallableWithPaths<A,?>> clazz, Object... clazzConstructorArguments) {
		super(true);
		this.clazz = clazz;
		this.clazzConstructorArguments = clazzConstructorArguments;
		this.executor = new ExecutorServiceProvider(executor);
		executorGiven = true;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public Boolean processItem(List<A> input) {
		if (executorGiven) {
			//declare a threaded list processor
			ThreadedElementProcessor<A> processor = new ThreadedElementProcessor<A>(executor.getExecutorService(), clazz, clazzConstructorArguments);
			
			for (A element : input) {
				processor.processElement(element);
			}

			return true;
		} else {
			//declare a threaded list processor
			ThreadedElementProcessor<A> processor = new ThreadedElementProcessor<A>(threadCount, clazz, clazzConstructorArguments);

			for (A element : input) {
				processor.processElement(element);
			}

			//we are done! Shutdown of the executor service is necessary! (That means: No new task submissions!)
			return processor.getExecutorServiceProvider().shutdownAndWaitForTermination();
		}
	}

	@Override
	public boolean finalShutdown() {
		if (executorGiven) {
			//we are done! Shutdown of the executor service is necessary! (That means: No new task submissions!)
			return executor.shutdownAndWaitForTermination();
		}
		return true;
	}
	
	

}
