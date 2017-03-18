/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threaded;

import java.nio.file.*;

import se.de.hu_berlin.informatik.utils.files.AFileWalker;
import se.de.hu_berlin.informatik.utils.processors.sockets.ConsumingProcessorSocketGenerator;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.DisruptorProvider;

/**
 * {@link AFileWalker} extension that takes a callable class 
 * and calls it on every file visited that matches the given pattern.
 * 
 * @author Simon Heiden
 */
public class ThreadedFileWalker extends AFileWalker {
	
	private DisruptorProvider<Path> disruptorProvider;

	private ThreadedFileWalker(Builder builder) {
		super(builder);
		
		if (builder.callableFactory == null) {
			throw new IllegalStateException("No callable class given.");
		}
		
		disruptorProvider = new DisruptorProvider<>(builder.classLoader);
		disruptorProvider.connectHandlers(builder.callableFactory, builder.threadCount);
	}

	/**
	 * @return
	 * the disruptor provider
	 */
	public DisruptorProvider<Path> getDisruptorProvider() {
		return disruptorProvider;
	}
	
	public void shutdown() {
		disruptorProvider.shutdown();
	}
	
	@Override
	public void processMatchedFileOrDir(Path fileOrDir) {
//		Log.out(this, "\tsubmitting task for: " + file);
		disruptorProvider.submit(fileOrDir);
	}
	
	public static class Builder extends AFileWalker.Builder {

		public ConsumingProcessorSocketGenerator<Path> callableFactory;
		public int threadCount;
		public ClassLoader classLoader;
		
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
		 * a factory that provides instances of event handlers 
		 * @return
		 * this
		 */
		public Builder call(ConsumingProcessorSocketGenerator<Path> callableFactory) {
			this.callableFactory = callableFactory;
			return this;
		}
		
		/**
		 * Sets a class loader to set for created threads.
		 * @param classLoader
		 * a class loader 
		 * @return
		 * this
		 */
		public Builder setClassLoader(ClassLoader classLoader) {
			this.classLoader = classLoader;
			return this;
		}
		
	}
}
