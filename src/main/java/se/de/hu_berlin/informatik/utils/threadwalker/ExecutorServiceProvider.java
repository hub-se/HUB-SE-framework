/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threadwalker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Simon Heiden
 */
public class ExecutorServiceProvider {

	final private ExecutorService executor;
	
	/**
	 * Creates an {@link ExecutorServiceProvider} object with the given parameters.
	 * @param executor
	 * an executor service that shall be used
	 */
	public ExecutorServiceProvider(ExecutorService executor) {
		super();
		this.executor = executor;
	}
	
	/**
	 * Creates an {@link ExecutorServiceProvider} object with the given parameters.
	 * @param corePoolSize
	 * the number of threads to keep in the pool, even if they are idle, unless allowCoreThreadTimeOut is set
	 * @param maximumPoolSize
	 * the maximum number of threads to allow in the pool
	 * @param keepAliveTime
	 * when the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating.
	 * @param unit the time unit for the keepAliveTime argument
	 */
	public ExecutorServiceProvider(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
		super();
		//create an executor service
		this.executor = new ThreadPoolExecutor(
				corePoolSize, maximumPoolSize, keepAliveTime, unit,
                new LimitedQueue<Runnable>(2*maximumPoolSize));
	}
	
	/**
	 * Creates an {@link ExecutorServiceProvider} object with the given parameters,
	 * {@code keepAliveTime=1L} and {@code unit=TimeUnit.SECONDS}.
	 * @param corePoolSize
	 * the number of threads to keep in the pool, even if they are idle, unless allowCoreThreadTimeOut is set
	 * @param maximumPoolSize
	 * the maximum number of threads to allow in the pool
	 */
	public ExecutorServiceProvider(int corePoolSize, int maximumPoolSize) {
		this(corePoolSize, maximumPoolSize, 1L, TimeUnit.SECONDS);
	}
	
	/**
	 * Creates an {@link ExecutorServiceProvider} object with the given parameters,
	 * {@code corePoolSize=1}, {@code keepAliveTime=1L} and {@code unit=TimeUnit.SECONDS}.
	 * @param maximumPoolSize
	 * the maximum number of threads to allow in the pool
	 */
	public ExecutorServiceProvider(int maximumPoolSize) {
		this(1, maximumPoolSize, 1L, TimeUnit.SECONDS);
	}

	/**
	 * @return
	 * the executor service
	 */
	public ExecutorService getExecutor() {
		return executor;
	}
	
}
