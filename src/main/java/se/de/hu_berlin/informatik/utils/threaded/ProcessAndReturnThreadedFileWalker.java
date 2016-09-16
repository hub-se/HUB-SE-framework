/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threaded;

import java.nio.file.*;
import java.lang.reflect.InvocationTargetException;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
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
	
	private Class<? extends CallableWithReturn<Path,B>> call;
	private Class<?>[] typeArgs;
	private Object[] clazzConstructorArguments;
	private APipe<?, B> pipe;

	private ProcessAndReturnThreadedFileWalker(Builder<B> builder) {
		super(builder);
		
		call = builder.call;
		typeArgs = builder.typeArgs;
		clazzConstructorArguments = builder.clazzConstructorArguments;
		pipe = builder.pipe;
		
		if (call == null) {
			throw new IllegalStateException("No callable class given.");
		}
		if (pipe == null) {
			throw new IllegalStateException("No callback pipe given.");
		}
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.threadwalker.AThreadedFileWalker#processMatchedFileOrDir(java.nio.file.Path)
	 */
	@Override
	public void processMatchedFileOrDir(Path fileOrDir) {
//		Misc.out(this, "\tsubmitting task for: " + fileOrDir);
		try {
			CallableWithReturn<Path,B> o = call.getConstructor(typeArgs).newInstance(clazzConstructorArguments);
			o.setPipe(pipe);
			o.setInput(fileOrDir);
			getExecutorService().submit(o);
		} catch (InstantiationException e) {
			Log.err(this, e, "Cannot instantiate object %s.", call.getSimpleName());
		} catch (IllegalAccessException e) {
			Log.err(this, e, "Illegal access to object %s.", call.getSimpleName());
		} catch (IllegalArgumentException e) {
			Log.abort(this, e, "Illegal argument to object %s.", call.getSimpleName());
		} catch (InvocationTargetException e) {
			Log.err(this, e, "Invocation target exception on object %s.", call.getSimpleName());
		} catch (NoSuchMethodException e) {
			Log.abort(this, e, "No such method exception on object %s.", call.getSimpleName());
		} catch (SecurityException e) {
			Log.err(this, e, "Security exception on object %s.", call.getSimpleName());
		}
	}

	public static class Builder<B> extends AThreadedFileWalker.Builder {

		private Class<? extends CallableWithReturn<Path,B>> call = null;
		private Class<?>[] typeArgs = null;
		private Object[] clazzConstructorArguments = null;
		private APipe<?, B> pipe = null;
		
		public Builder(String pattern) {
			super(pattern);
		}

		@Override
		public AThreadedFileWalker build() {
			return new ProcessAndReturnThreadedFileWalker<>(this);
		}
		
		/**
		 * Sets the callable class with its contructor arguments.
		 * @param callableClass
		 * callable class to be called on every visited file
		 * @param clazzConstructorArguments
		 * arguments that shall be passed to the constructor of the callable class 
		 * @return
		 * this
		 */
		public Builder<B> call(Class<? extends CallableWithReturn<Path,B>> callableClass, Object... clazzConstructorArguments) {
			this.call = callableClass;
			this.typeArgs = call.getConstructors()[0].getParameterTypes();//TODO is that right?
			this.clazzConstructorArguments = clazzConstructorArguments;
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
