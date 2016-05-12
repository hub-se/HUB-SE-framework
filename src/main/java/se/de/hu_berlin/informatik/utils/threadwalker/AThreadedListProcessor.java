/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threadwalker;

import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * {@link SimpleFileVisitor} extension that is enriched with a blocking {@link ExecutorService}.
 * 
 * @author Simon Heiden
 */
public abstract class AThreadedListProcessor extends SimpleFileVisitor<Path> {

	final private ExecutorServiceProvider executor;
	
	/**
	 * Creates an {@link AThreadedListProcessor} object with the given parameters.
	 * @param corePoolSize
	 * the number of threads to keep in the pool, even if they are idle, unless allowCoreThreadTimeOut is set
	 * @param maximumPoolSize
	 * the maximum number of threads to allow in the pool
	 * @param keepAliveTime
	 * when the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating.
	 * @param unit the time unit for the keepAliveTime argument
	 */
	public AThreadedListProcessor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
		super();
		//create an executor service
		this.executor = new ExecutorServiceProvider(corePoolSize, maximumPoolSize, keepAliveTime, unit);
	}
	
	/**
	 * Creates an {@link AThreadedListProcessor} object with the given parameters,
	 * {@code keepAliveTime=1L} and {@code unit=TimeUnit.SECONDS}.
	 * @param corePoolSize
	 * the number of threads to keep in the pool, even if they are idle, unless allowCoreThreadTimeOut is set
	 * @param maximumPoolSize
	 * the maximum number of threads to allow in the pool
	 */
	public AThreadedListProcessor(int corePoolSize, int maximumPoolSize) {
		this(corePoolSize, maximumPoolSize, 1L, TimeUnit.SECONDS);
	}
	
	/**
	 * Creates an {@link AThreadedListProcessor} object with the given parameters,
	 * {@code corePoolSize=1}, {@code keepAliveTime=1L} and {@code unit=TimeUnit.SECONDS}.
	 * @param maximumPoolSize
	 * the maximum number of threads to allow in the pool
	 */
	public AThreadedListProcessor(int maximumPoolSize) {
		this(1, maximumPoolSize, 1L, TimeUnit.SECONDS);
	}
	
	/**
	 * Creates an {@link AThreadedListProcessor} object with the given parameters
	 * @param executor
	 * an executor service that shall be used
	 */
	public AThreadedListProcessor(ExecutorService executor) {
		//create an executor service
		this.executor = new ExecutorServiceProvider(executor);
	}

	/**
	 * @return
	 * the executor service
	 */
	public ExecutorService getExecutor() {
		return executor.getExecutor();
	}

	
	abstract public void processElement(Object inputObject);
	
}
