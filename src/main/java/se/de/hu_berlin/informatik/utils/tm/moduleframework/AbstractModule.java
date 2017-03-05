/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.moduleframework;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInputAndReturn;
import se.de.hu_berlin.informatik.utils.tm.AbstractTransmitter;
import se.de.hu_berlin.informatik.utils.tm.Consumer;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.ModuleLinker;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipe;

/**
 * An abstract class that provides basic functionalities of a modular
 * framework. Classes that extend this abstract class may have a single input 
 * and/or an output object at any point in time and can be linked together 
 * such that one module provides the input of another module. 
 * Modules may even be reused when properly implemented.
 * 
 * <br><br> For convenience, multiple (matching) modules may be linked together 
 * like this:
 * 
 * <br><br> {@code module1.linkTo(module2).linkTo(module3).linkTo(...)...;}
 * 
 * <br><br> which will link the output of {@code module1} to the input of 
 * {@code module2} and then link the output of {@code module2} to the input of
 * {@code module3}, etc.
 * 
 * <br><br> After linking, any matching item submitted to the first module
 * will start the execution process. The end result can be obtained from the
 * last linked module via the {@code getResult()} method. In theory, one could
 * also obtain the intermediate results from each of the linked modules if
 * needed.
 * 
 * <br><br> In general, modules should not be linked manually and should
 * preferably be linked together with a {@link ModuleLinker} which provides
 * more general and easy access methods.
 * 
 * @author Simon Heiden
 *
 * @param <A>
 * is the type of the input object
 * @param <B>
 * is the type of the output object
 * 
 * @see ModuleLinker
 */
public abstract class AbstractModule<A,B> extends AbstractTransmitter<A,B> {
	
	private B output = null;
	
	private AbstractModule<B,?> linkedModule = null;
	
	private boolean needsInput = false;
	
	/**
	 * Creates a new module with the given parameter.
	 * @param needsInput
	 * determines if the module needs an input item to function
	 */
	public AbstractModule(boolean needsInput) {
		super();
		this.needsInput = needsInput;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <C> Consumer<C> linkTo(Consumer<C> consumer) throws IllegalArgumentException, IllegalStateException {
		if (consumer instanceof AbstractModule) {
			return linkModuleTo((AbstractModule<C, ?>)consumer);
		} else {
			throw new IllegalStateException("Can only link to other modules.");
		}
	}

	/**
	 * Links a matching module to the output of this module.
	 * @param <C>
	 * the input type of the module to be linked to
	 * @param module
	 * the module to be linked to
	 * @return
	 * the module to be linked to
	 * @throws IllegalArgumentException
	 * if the input type C of the given module does not match the output type B of this module
	 */
	@SuppressWarnings("unchecked")
	private <C> AbstractModule<C,?> linkModuleTo(AbstractModule<C,?> module) throws IllegalArgumentException {
		try {
			this.linkedModule = (AbstractModule<B,?>) module;
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("Type mismatch while linking to other module.", e);
		}
		return module;
	}

	/**
	 * Submits the given item to the module and processes it.
	 * @param item
	 * the item to be processed by the module
	 * @return
	 * this module
	 */
	@SuppressWarnings("unchecked")
	public AbstractModule<A,B> submit(Object item) {
		if (needsInput && item == null) {
			Log.err(this, "No input item submitted/available.");
		} else {
			try {			
				track();
				consume((A)item);
			} catch (ClassCastException e) {
				Log.abort(this, e, "Type mismatch while submitting!");
			}
			if (linkedModule != null) {
				linkedModule.submit(output);
			}
		}
		return this;
	}
	
	@Override
	public void produce(B item) {
		output = item;
	}
	
	/**
	 * Starts processing without an input item.
	 * @return
	 * this module
	 */
	public AbstractModule<A,B> start() {
		return submit(null);
	}

	/**
	 * @return
	 * the last output value (if any) or null, 
	 * if no output was produced so far
	 */
	public B getResult() {
		return output;
	}
	
	/**
	 * Aborts if no linked module is available.
	 * @return
	 * the module that this module is linked to
	 */
	public AbstractModule<B,?> getLinkedModule() {
		if (linkedModule == null) {
			Log.abort(this, "No module linked to.");
		}
		return linkedModule;
	}

//	@Override
//	public AbstractPipe<A, B> asPipe() throws UnsupportedOperationException {
//		throw new UnsupportedOperationException("not supported");
//	}

	@Override
	public AbstractModule<A, B> asModule() throws UnsupportedOperationException {
		return this;
	}

//	@Override
//	public EHWithInputAndReturn<A, B> asEH() throws UnsupportedOperationException {
//		throw new UnsupportedOperationException("not supported");
//	}
	
}
