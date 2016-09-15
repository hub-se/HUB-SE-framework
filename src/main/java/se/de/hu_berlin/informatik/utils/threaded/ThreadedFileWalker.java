/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threaded;

import java.nio.file.*;
import java.lang.reflect.InvocationTargetException;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;

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
	
	private final Class<? extends CallableWithPaths<Path,?>> call;
	private final Class<?>[] typeArgs;
	private final Object[] clazzConstructorArguments;
	
	private ThreadedFileWalker(Builder builder) {
		super(builder);
		
		call = builder.call;
		typeArgs = builder.typeArgs;
		clazzConstructorArguments = builder.clazzConstructorArguments;
		
		if (call == null) {
			throw new IllegalStateException("No callable class given.");
		}
	}

	@Override
	public void processMatchedFileOrDir(Path fileOrDir) {
//		Misc.out(this, "\tsubmitting task for: " + file);
		try {
			CallableWithPaths<Path,?> o = call.getConstructor(typeArgs).newInstance(clazzConstructorArguments);
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
	
	public static class Builder extends AThreadedFileWalker.Builder {

		private Class<? extends CallableWithPaths<Path,?>> call = null;
		private Class<?>[] typeArgs = null;
		private Object[] clazzConstructorArguments = null;
		
		public Builder(String pattern) {
			super(pattern);
		}

		@Override
		public AThreadedFileWalker build() {
			return new ThreadedFileWalker(this);
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
		public Builder call(Class<? extends CallableWithPaths<Path,?>> callableClass, Object... clazzConstructorArguments) {
			this.call = callableClass;
			this.typeArgs = call.getConstructors()[0].getParameterTypes();//TODO is that right?
			this.clazzConstructorArguments = clazzConstructorArguments;
			return this;
		}
		
	}
}
