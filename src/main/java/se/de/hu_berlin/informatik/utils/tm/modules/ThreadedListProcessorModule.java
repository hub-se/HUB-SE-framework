/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.modules;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import se.de.hu_berlin.informatik.utils.threadwalker.GenerateOutputThreadedFileWalker;
import se.de.hu_berlin.informatik.utils.threadwalker.GenerateOutputThreadedListProcessor;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AModule;
import se.de.hu_berlin.informatik.utils.threadwalker.CallableWithPaths;

/**
 * Starts a threaded file walker with a provided callable class on a submitted input path.
 * 
 * @author Simon Heiden
 * 
 * @see GenerateOutputThreadedFileWalker
 * @see CallableWithPaths
 * @see Callable
 */
public class ThreadedListProcessorModule<A> extends AModule<List<A>,Boolean> {

	private int threadCount;
	private ExecutorService executor = null;
	private boolean executorGiven = false;
	private Class<? extends CallableWithPaths<A,?>> clazz;
	private Path outputdir = null;
	private Object[] clazzConstructorArguments;
	boolean generateOutputPaths;
	private String extension;
	private boolean overwrite;
	
	/**
	 * Creates a new {@link ThreadedListProcessorModule} object with the given parameters. 
	 * Automatically generates output paths.
	 * @param outputdir
	 * an output directory
	 * @param extension
	 * the extension of automatically generated output paths
	 * @param threadCount
	 * the maximal number of threads that shall be run in parallel
	 * @param overwrite
	 * determines if files and directories should be overwritten
	 * @param clazz
	 * a {@link CallableWithPaths} class which is called for every matching file
	 * @param clazzConstructorArguments
	 * arguments that might be needed in the constructor of the callable class
	 */
	public ThreadedListProcessorModule(Path outputdir, String extension, int threadCount, boolean overwrite,
			Class<? extends CallableWithPaths<A,?>> clazz, Object... clazzConstructorArguments) {
		super(true);
		this.threadCount = threadCount;
		this.overwrite = overwrite;
		this.clazz = clazz;
		this.outputdir = outputdir;
		this.extension = extension;
		this.clazzConstructorArguments = clazzConstructorArguments;
	}
	
	/**
	 * Creates a new {@link ThreadedListProcessorModule} object with the given parameters. 
	 * Doesn't create any output paths and assumes that no output is created during execution.
	 * @param threadCount
	 * the maximal number of threads that shall be run in parallel
	 * @param clazz
	 * a {@link CallableWithPaths} class which is called for every matching file
	 * @param clazzConstructorArguments
	 * arguments that might be needed in the constructor of the callable class
	 */
	public ThreadedListProcessorModule(int threadCount, Class<? extends CallableWithPaths<A,?>> clazz, Object... clazzConstructorArguments) {
		this(null, null, threadCount, false, clazz, clazzConstructorArguments);
	}
	
	/**
	 * Creates a new {@link ThreadedListProcessorModule} object with the given parameters. 
	 * Automatically generates output paths.
	 * @param executor
	 * an executor service that shall be used
	 * @param outputdir
	 * an output directory
	 * @param extension
	 * the extension of automatically generated output paths
	 * @param overwrite
	 * determines if files and directories should be overwritten
	 * @param clazz
	 * a {@link CallableWithPaths} class which is called for every matching file
	 * @param clazzConstructorArguments
	 * arguments that might be needed in the constructor of the callable class
	 */
	public ThreadedListProcessorModule(ExecutorService executor, Path outputdir, String extension, boolean overwrite,
			Class<? extends CallableWithPaths<A,?>> clazz, Object... clazzConstructorArguments) {
		super(true);
		this.overwrite = overwrite;
		this.clazz = clazz;
		this.outputdir = outputdir;
		this.extension = extension;
		this.clazzConstructorArguments = clazzConstructorArguments;
		this.executor = executor;
		executorGiven = true;
	}
	
	/**
	 * Creates a new {@link ThreadedListProcessorModule} object with the given parameters. 
	 * Doesn't create any output paths and assumes that no output is created during execution.
	 * @param executor
	 * an executor service that shall be used
	 * @param clazz
	 * a {@link CallableWithPaths} class which is called for every matching file
	 * @param clazzConstructorArguments
	 * arguments that might be needed in the constructor of the callable class
	 */
	public ThreadedListProcessorModule(ExecutorService executor, Class<? extends CallableWithPaths<A,?>> clazz, Object... clazzConstructorArguments) {
		this(executor, null, null, false, clazz, clazzConstructorArguments);
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public Boolean processItem(List<A> input) {
		if (executorGiven) {
			//declare a threaded list processor
			GenerateOutputThreadedListProcessor<A> walker;
			if (outputdir != null) {
				walker = new GenerateOutputThreadedListProcessor<A>(executor, outputdir, extension, overwrite, clazz, clazzConstructorArguments);
			} else {
				walker = new GenerateOutputThreadedListProcessor<A>(executor, clazz, clazzConstructorArguments);
			}

			for (A element : input) {
				walker.processElement(element);
			}

			return true;
		} else {
			//declare a threaded list processor
			GenerateOutputThreadedListProcessor<A> walker;
			if (outputdir != null) {
				walker = new GenerateOutputThreadedListProcessor<A>(outputdir, extension, threadCount, overwrite, clazz, clazzConstructorArguments);
			} else {
				walker = new GenerateOutputThreadedListProcessor<A>(threadCount, clazz, clazzConstructorArguments);
			}

			for (A element : input) {
				walker.processElement(element);
			}

			//we are done! Shutdown of the executor service is necessary! (That means: No new task submissions!)
			walker.getExecutor().shutdown();

			//await termination
			boolean result = false;
			try {
				//			System.out.println("awaiting termination...");
				result = walker.getExecutor().awaitTermination(7, TimeUnit.DAYS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return result;
		}
	}

	@Override
	public boolean finalShutdown() {
		if (executorGiven) {
			//we are done! Shutdown of the executor service is necessary! (That means: No new task submissions!)
			executor.shutdown();

			//await termination
			boolean result = false;
			try {
				//			System.out.println("awaiting termination...");
				result = executor.awaitTermination(7, TimeUnit.DAYS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return result;
		}
		return true;
	}
	
	

}
