/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threaded;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;

/**
 * Convenience access to (blocking) executor services with easy shutdown.
 * 
 * @author Simon Heiden
 */
public class ExecutorServiceProvider {

	public static final Callable<Boolean> TRIGGER = new Callable<Boolean>() {

		@Override
		public Boolean call() {
			throw new IllegalAccessError("Trigger object should not be executed!");
		}
	};

	final private ExecutorService executor;

	/**
	 * Creates an {@link ExecutorServiceProvider} object with the given
	 * parameters.
	 * @param executor
	 * an executor service that shall be used
	 */
	public ExecutorServiceProvider(ExecutorService executor) {
		super();
		this.executor = executor;
	}

	/**
	 * Creates an {@link ExecutorServiceProvider} object with the given
	 * parameters.
	 * @param corePoolSize
	 * the number of threads to keep in the pool, even if they are idle, unless
	 * allowCoreThreadTimeOut is set
	 * @param maximumPoolSize
	 * the maximum number of threads to allow in the pool
	 * @param keepAliveTime
	 * when the number of threads is greater than the core, this is the maximum
	 * time that excess idle threads will wait for new tasks before terminating.
	 * @param unit
	 * the time unit for the keepAliveTime argument
	 * @param cl
	 * a class loader to set as the context class loader for created threads
	 */
	public ExecutorServiceProvider(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			ClassLoader cl) {
		super();
		LimitedTriggerQueue<Runnable, Callable<Boolean>> workQueue = new LimitedTriggerQueue<>(2 * maximumPoolSize, TRIGGER);
		// create an executor service
		this.executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				new ThreadFactory() {

					ThreadFactory factory = Executors.defaultThreadFactory();

					// int counter = 0;
					@Override
					public Thread newThread(Runnable r) {
						// ++counter;
						// Log.out(this, "Creating Thread no. %d for %s.",
						// counter, r);
						Thread thread = factory.newThread(r);
						if (cl != null) {
							thread.setContextClassLoader(cl);
						}
						return thread;
					}
				});
		workQueue.setItemToOfferAfterTrigger(new ExecutorServiceShutDownNotice(this.executor));
	}

	/**
	 * Creates an {@link ExecutorServiceProvider} object with the given
	 * parameters, {@code keepAliveTime=1L} and {@code unit=TimeUnit.SECONDS}.
	 * @param corePoolSize
	 * the number of threads to keep in the pool, even if they are idle, unless
	 * allowCoreThreadTimeOut is set
	 * @param maximumPoolSize
	 * the maximum number of threads to allow in the pool
	 * @param cl
	 * a class loader to set as the context class loader for created threads
	 */
	public ExecutorServiceProvider(int corePoolSize, int maximumPoolSize, ClassLoader cl) {
		this(corePoolSize, maximumPoolSize, 1L, TimeUnit.SECONDS, cl);
	}

	/**
	 * Creates an {@link ExecutorServiceProvider} object with the given fixed
	 * number of threads, {@code keepAliveTime=1L} and
	 * {@code unit=TimeUnit.SECONDS}.
	 * @param poolSize
	 * the number of threads to run in the pool
	 * @param cl
	 * a class loader to set as the context class loader for created threads
	 */
	public ExecutorServiceProvider(int poolSize, ClassLoader cl) {
		this(poolSize, poolSize, 1L, TimeUnit.SECONDS, cl);
	}

	/**
	 * Creates an {@link ExecutorServiceProvider} object with the given
	 * parameters.
	 * @param corePoolSize
	 * the number of threads to keep in the pool, even if they are idle, unless
	 * allowCoreThreadTimeOut is set
	 * @param maximumPoolSize
	 * the maximum number of threads to allow in the pool
	 * @param keepAliveTime
	 * when the number of threads is greater than the core, this is the maximum
	 * time that excess idle threads will wait for new tasks before terminating.
	 * @param unit
	 * the time unit for the keepAliveTime argument
	 */
	public ExecutorServiceProvider(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
		this(corePoolSize, maximumPoolSize, keepAliveTime, unit, null);
	}

	/**
	 * Creates an {@link ExecutorServiceProvider} object with the given
	 * parameters, {@code keepAliveTime=1L} and {@code unit=TimeUnit.SECONDS}.
	 * @param corePoolSize
	 * the number of threads to keep in the pool, even if they are idle, unless
	 * allowCoreThreadTimeOut is set
	 * @param maximumPoolSize
	 * the maximum number of threads to allow in the pool
	 */
	public ExecutorServiceProvider(int corePoolSize, int maximumPoolSize) {
		this(corePoolSize, maximumPoolSize, 1L, TimeUnit.SECONDS, null);
	}

	/**
	 * Creates an {@link ExecutorServiceProvider} object with the given fixed
	 * number of threads, {@code keepAliveTime=1L} and
	 * {@code unit=TimeUnit.SECONDS}.
	 * @param poolSize
	 * the number of threads to run in the pool
	 */
	public ExecutorServiceProvider(int poolSize) {
		this(poolSize, poolSize, 1L, TimeUnit.SECONDS, null);
	}

	/**
	 * @return the executor service
	 */
	public ExecutorService getExecutorService() {
		return executor;
	}

	/**
	 * Shuts down the underlying executor service and waits 7 days for it to
	 * terminate.
	 * @param log
	 * whether to create an output message
	 * @return true if all jobs finished, false if interrupted or a timeout has
	 * been reached
	 */
	public boolean shutdownAndWaitForTermination(boolean log) {
		return shutdownAndWaitForTermination(7, TimeUnit.DAYS, log);
	}

	/**
	 * Shuts down the underlying executor service and waits 7 days for it to
	 * terminate. Print an output message.
	 * @return true if all jobs finished, false if interrupted or a timeout has
	 * been reached
	 */
	public boolean shutdownAndWaitForTermination() {
		return shutdownAndWaitForTermination(7, TimeUnit.DAYS, true);
	}

	/**
	 * Shuts down the underlying executor service and waits for it to terminate.
	 * @param duration
	 * the number of time units to wait
	 * @param unit
	 * the time unit used (seconds, minutes, ...)
	 * @param log
	 * whether to create an output message
	 * @return true if all jobs finished, false if interrupted or a timeout has
	 * been reached
	 */
	public boolean shutdownAndWaitForTermination(int duration, TimeUnit unit, boolean log) {
		// we are done! Shutdown of the executor service is necessary!
		// to prevent pending tasks to be
		executor.submit(TRIGGER);

		// await termination
		boolean result = false;
		boolean terminated = false;
		while (!terminated) {
			try {
				// Log.out(this, "awaiting termination...");
				result = executor.awaitTermination(duration, unit);
				terminated = true;
			} catch (InterruptedException e) {
				// try again...
			}
		}

		if (result) {
			if (log) {
				Log.out(this, "All jobs finished!");
			}
		} else {
			Log.err(this, "Timeout reached or Exception thrown! Could not finish all jobs!");
			executor.shutdownNow();
		}

		return result;
	}

	private static class ExecutorServiceShutDownNotice implements Runnable {

		private ExecutorService executor;

		public ExecutorServiceShutDownNotice(ExecutorService executor) {
			this.executor = executor;
		}

		@Override
		public void run() {
			Log.out(this, "awaiting termination...");
			// from this point on: No new task submissions!
			executor.shutdown();
		}

	}

}
