/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.modules;

import java.util.concurrent.ExecutorService;
import java.lang.reflect.InvocationTargetException;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.threaded.CallableWithPaths;
import se.de.hu_berlin.informatik.utils.threaded.ExecutorServiceProvider;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AModule;

/**
 * Module that is given a callable class 
 * and calls it on every item that is given to it.
 * 
 * @author Simon Heiden
 * 
 * @see AModule
 * @see CallableWithPaths
 *
 * @param <A>
 * type of input objects
 */
public class ThreadedElementProcessorModule<A> extends AModule<A,A>{
	
	Class<? extends CallableWithPaths<A,?>> call;
	Class<?>[] typeArgs;
	Object[] clazzConstructorArguments;
	
	private ExecutorServiceProvider executor;
	
	/**
	 * Initializes a {@link ThreadedElementProcessorModule} object with the given parameters.
	 * @param threadCount
	 * sets the thread count of the underlying {@link java.util.concurrent.ExecutorService}
	 * @param callableClass
	 * callable class to be called on every visited file
	 * @param clazzConstructorArguments
	 * arguments that shall be passed to the constructor of the callable class 
	 */
	public ThreadedElementProcessorModule(int threadCount,
			Class<? extends CallableWithPaths<A,?>> callableClass, Object... clazzConstructorArguments) {
		super(true);
		//create an executor service
		this.executor = new ExecutorServiceProvider(threadCount);
		this.call = callableClass;
		this.typeArgs = call.getConstructors()[0].getParameterTypes();//TODO is that right?
		this.clazzConstructorArguments = clazzConstructorArguments;
	}
	
	/**
	 * Initializes a {@link ThreadedElementProcessorModule} object with the given parameters.
	 * @param executor
	 * an executor service that shall be used
	 * @param callableClass
	 * callable class to be called on every visited file
	 * @param clazzConstructorArguments
	 * arguments that shall be passed to the constructor of the callable class 
	 */
	public ThreadedElementProcessorModule(ExecutorService executor,
			Class<? extends CallableWithPaths<A,?>> callableClass, Object... clazzConstructorArguments) {
		super(true);
		//create an executor service
		this.executor = new ExecutorServiceProvider(executor);
		this.call = callableClass;
		this.typeArgs = call.getConstructors()[0].getParameterTypes();//TODO is that right?
		this.clazzConstructorArguments = clazzConstructorArguments;
	}
	

	@SuppressWarnings("unchecked")
	private void processElement(Object inputObject) {
//		Misc.out(this, "\tsubmitting task for: " + inputObject);
		try {
			CallableWithPaths<A,?> o = call.getConstructor(typeArgs).newInstance(clazzConstructorArguments);
			o.setInput((A) inputObject);
			executor.getExecutorService().submit(o);
		} catch (ClassCastException e) {
			Log.abort(this, e, "Input type mismatch!");
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
	
	/**
	 * @return
	 * the executor service provider
	 */
	public ExecutorServiceProvider getExecutorServiceProvider() {
		return executor;
	}

	@Override
	public A processItem(A item) {
		processElement(item);
		return item;
	}
}
