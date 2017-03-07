/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threaded;

import java.nio.file.*;

import se.de.hu_berlin.informatik.utils.fileoperations.AFileWalker;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.DisruptorProvider;
import se.de.hu_berlin.informatik.utils.tm.user.ConsumingProcessorUserGenerator;

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
		
		disruptorProvider = new DisruptorProvider<>();
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

		public ConsumingProcessorUserGenerator<Path> callableFactory;
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
		 * a factory that provides instances of event handlers 
		 * @return
		 * this
		 */
		public Builder call(ConsumingProcessorUserGenerator<Path> callableFactory) {
			this.callableFactory = callableFactory;
			return this;
		}
		
	}
}
