/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threaded;

import java.util.concurrent.ExecutorService;
import java.lang.reflect.InvocationTargetException;

import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;

/**
 * {@link AThreadedProcessor} extension that takes a callable class 
 * and calls it on every item that is given to it.
 * 
 * @author Simon Heiden
 * 
 * @see AThreadedProcessor
 * @see CallableWithPaths
 *
 * @param <A>
 * type of input objects
 */
public class ThreadedElementProcessor<A> extends AThreadedProcessor {
	
	Class<? extends CallableWithPaths<A,?>> call;
	Class<?>[] typeArgs;
	Object[] clazzConstructorArguments;
	
	/**
	 * Initializes a {@link ThreadedElementProcessor} object with the given parameters.
	 * @param threadCount
	 * sets the thread count of the underlying {@link java.util.concurrent.ExecutorService}
	 * @param callableClass
	 * callable class to be called on every visited file
	 * @param clazzConstructorArguments
	 * arguments that shall be passed to the constructor of the callable class 
	 */
	public ThreadedElementProcessor(int threadCount,
			Class<? extends CallableWithPaths<A,?>> callableClass, Object... clazzConstructorArguments) {
		super(threadCount);
		this.call = callableClass;
		this.typeArgs = call.getConstructors()[0].getParameterTypes();//TODO is that right?
		this.clazzConstructorArguments = clazzConstructorArguments;
	}
	
	/**
	 * Initializes a {@link ThreadedElementProcessor} object with the given parameters.
	 * @param executor
	 * an executor service that shall be used
	 * @param callableClass
	 * callable class to be called on every visited file
	 * @param clazzConstructorArguments
	 * arguments that shall be passed to the constructor of the callable class 
	 */
	public ThreadedElementProcessor(ExecutorService executor,
			Class<? extends CallableWithPaths<A,?>> callableClass, Object... clazzConstructorArguments) {
		super(executor);
		this.call = callableClass;
		this.typeArgs = call.getConstructors()[0].getParameterTypes();//TODO is that right?
		this.clazzConstructorArguments = clazzConstructorArguments;
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public void processElement(Object inputObject) {
//		System.out.println("\tsubmitting task for: " + inputObject);
		try {
			CallableWithPaths<A,?> o = call.getConstructor(typeArgs).newInstance(clazzConstructorArguments);
			o.setInput((A) inputObject);
			getExecutorService().submit(o);
		} catch (ClassCastException e) {
			Misc.abort(this, e, "Input type mismatch!");
		} catch (InstantiationException e) {
			Misc.err(this, e, "Cannot instantiate object %s.", call.getSimpleName());
		} catch (IllegalAccessException e) {
			Misc.err(this, e, "Illegal access to object %s.", call.getSimpleName());
		} catch (IllegalArgumentException e) {
			Misc.abort(this, e, "Illegal argument to object %s.", call.getSimpleName());
		} catch (InvocationTargetException e) {
			Misc.err(this, e, "Invocation target exception on object %s.", call.getSimpleName());
		} catch (NoSuchMethodException e) {
			Misc.abort(this, e, "No such method exception on object %s.", call.getSimpleName());
		} catch (SecurityException e) {
			Misc.err(this, e, "Security exception on object %s.", call.getSimpleName());
		}
	}
	
}
