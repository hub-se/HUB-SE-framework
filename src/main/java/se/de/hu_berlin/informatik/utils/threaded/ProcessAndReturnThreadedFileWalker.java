/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threaded;

import java.nio.file.*;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.APipe;

/**
 * {@link AThreadedFileWalker} extension that takes a callable class 
 * and calls it on every file visited that matches the given pattern.
 * 
 * @author Simon Heiden
 * 
 * @see AThreadedFileWalker
 * @see CallableWithReturn
 */
public class ProcessAndReturnThreadedFileWalker<B> extends AThreadedFileWalker {

	private ProcessAndReturnThreadedFileWalker(Builder<B> builder) {
		super(builder);

		if (builder.callableFactory == null) {
			throw new IllegalStateException("No callable class given.");
		}
		if (builder.pipe == null) {
			throw new IllegalStateException("No callback pipe given.");
		}
		
		builder.callableFactory.setCallbackPipe(builder.pipe);
		getDisruptorProvider().connectHandlers(builder.threadCount, builder.callableFactory);
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.threadwalker.AThreadedFileWalker#processMatchedFileOrDir(java.nio.file.Path)
	 */
	@Override
	public void processMatchedFileOrDir(Path fileOrDir) {
//		Misc.out(this, "\tsubmitting task for: " + fileOrDir);
		getDisruptorProvider().submit(fileOrDir);
	}

	public static class Builder<B> extends AThreadedFileWalker.Builder {

		public IDisruptorEventHandlerFactoryWCallback<Path,B> callableFactory;
		public int threadCount;
		public APipe<?, B> pipe;
		
		public Builder(String pattern, int threadCount) {
			super(pattern);
			this.threadCount = threadCount;
		}

		@Override
		public AThreadedFileWalker build() {
			return new ProcessAndReturnThreadedFileWalker<>(this);
		}
		
		/**
		 * Sets the factory.
		 * @param callableFactory
		 * a factory that provides instances of callable classes 
		 * @return
		 * this
		 */
		public Builder<B> call(IDisruptorEventHandlerFactoryWCallback<Path,B> callableFactory) {
			this.callableFactory = callableFactory;
			return this;
		}
		
		/**
		 * Sets the callback pipe.
		 * @param pipe
		 * the pipe object that is associated with this file walker
		 * @return
		 * this
		 */
		public Builder<B> pipe(APipe<?, B> pipe) {
			this.pipe = pipe;
			return this;
		}
		
	}
}
