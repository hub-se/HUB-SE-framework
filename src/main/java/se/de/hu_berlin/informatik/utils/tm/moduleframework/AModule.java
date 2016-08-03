/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.moduleframework;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.tm.ITransmitter;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.ModuleLinker;

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
public abstract class AModule<A,B> implements ITransmitter<A,B> {
	
	private A input = null;
	private B output = null;
	
	private AModule<?,?> linkedModule = null;
	
	private boolean needsInput = false;
	
	/**
	 * Creates a new module with the given parameter.
	 * @param needsInput
	 * determines if the module needs an input item to function
	 */
	public AModule(boolean needsInput) {
		super();
		this.needsInput = needsInput;
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.miscellaneous.ITransmitter#linkTo(se.de.hu_berlin.informatik.utils.miscellaneous.ITransmitter)
	 */
	@Override
	public <C, D> ITransmitter<C, D> linkTo(ITransmitter<C, D> transmitter) {
		if (transmitter instanceof AModule) {
			return linkModuleTo((AModule<C, D>)transmitter);
		} else {
			Log.abort(this, "Can only link to other modules.");
		}
		return null;
	}

	/**
	 * Links a matching module to the output of this module.
	 * @param <C>
	 * the input type of the module to be linked to
	 * @param <D>
	 * the output type of the module to be linked to
	 * @param module
	 * the module to be linked to
	 * @return
	 * the module to be linked to
	 */
	private <C,D> AModule<C,D> linkModuleTo(AModule<C,D> module) {
		this.linkedModule = module;
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
	public AModule<A,B> submit(Object item) {
		try {
			input = (A)item;
		} catch (ClassCastException e) {
			Log.abort(this, e, "Type mismatch while submitting!");
		}
		process();
		if (linkedModule != null) {
			linkedModule.submit(output);
		}
		return this;
	}
	
	/**
	 * Starts processing without an input item.
	 * @return
	 * this module
	 */
	public AModule<A,B> start() {
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
	public AModule<?,?> getLinkedModule() {
		if (linkedModule == null) {
			Log.abort(this, "No module linked to.");
		}
		return linkedModule;
	}

	/**
	 * Processes an available input item (if any) and sets the result as
	 * the output item of this module.
	 */
	private void process() {
		if (needsInput && input == null) {
			Log.err(this, "No input item submitted/available.");
			return;
		}
		output = processItem(input);
	}

}
