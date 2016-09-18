/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threaded;

import java.nio.file.*;

/**
 * {@link AThreadedFileWalker} extension that takes a callable class 
 * and calls it on every file visited that matches the given pattern.
 * 
 * @author Simon Heiden
 * 
 * @see AThreadedFileWalker
 * @see CallableWithPaths
 */
public class ThreadedFileWalker extends AThreadedFileWalker {
	
	private ThreadedFileWalker(Builder builder) {
		super(builder);
		
		if (builder.callableFactory == null) {
			throw new IllegalStateException("No callable class given.");
		}
		
		getDisruptorProvider().connectHandlers(builder.threadCount, builder.callableFactory);
	}

	@Override
	public void processMatchedFileOrDir(Path fileOrDir) {
//		Misc.out(this, "\tsubmitting task for: " + file);
		getDisruptorProvider().submit(fileOrDir);
	}
	
	public static class Builder extends AThreadedFileWalker.Builder {

		public IDisruptorEventHandlerFactory<Path> callableFactory;
		public int threadCount;
		
		public Builder(String pattern, int threadCount) {
			super(pattern);
			this.threadCount = threadCount;
		}

		@Override
		public AThreadedFileWalker build() {
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
