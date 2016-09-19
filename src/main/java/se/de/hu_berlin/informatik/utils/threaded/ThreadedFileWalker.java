/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threaded;

import java.nio.file.*;

import se.de.hu_berlin.informatik.utils.fileoperations.AFileWalker;

/**
 * {@link AFileWalker} extension that takes a callable class 
 * and calls it on every file visited that matches the given pattern.
 * 
 * @author Simon Heiden
 * 
 * @see CallableWithPaths
 */
public class ThreadedFileWalker extends AFileWalker {
	
	private DisruptorProvider<Path> disruptorProvider;

	private ThreadedFileWalker(Builder builder) {
		super(builder);
		
		if (builder.callableFactory == null) {
			throw new IllegalStateException("No callable class given.");
		}
		
		disruptorProvider = new DisruptorProvider<>(8);
		disruptorProvider.connectHandlers(builder.threadCount, builder.callableFactory);
	}

	/**
	 * @return
	 * the disruptor provider
	 */
	public DisruptorProvider<Path> getDisruptorProvider() {
		return disruptorProvider;
	}
	
	@Override
	public void processMatchedFileOrDir(Path fileOrDir) {
//		Log.out(this, "\tsubmitting task for: " + file);
		disruptorProvider.submit(fileOrDir);
		disruptorProvider.shutdown();
	}
	
	public static class Builder extends AFileWalker.Builder {

		public IDisruptorEventHandlerFactory<Path> callableFactory;
		public int threadCount;
		
		public Builder(String pattern, int threadCount) {
			super(pattern);
			this.threadCount = threadCount;
		}

		@Override
		public ThreadedFileWalker build() {
			return new ThreadedFileWalker(this);
		}
		
		/**
		 * Sets the factory.
		 * @param callableFactory
		 * a factory that provides instances of callable classes 
		 * @return
		 * this
		 */
		public Builder call(IDisruptorEventHandlerFactory<Path> callableFactory) {
			this.callableFactory = callableFactory;
			return this;
		}
		
	}
}
