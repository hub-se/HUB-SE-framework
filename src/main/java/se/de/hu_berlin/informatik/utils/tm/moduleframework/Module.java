/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.moduleframework;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.AbstractDisruptorEventHandler;
import se.de.hu_berlin.informatik.utils.tm.Processor;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.ModuleLinker;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.Pipe;
import se.de.hu_berlin.informatik.utils.tm.user.AbstractProcessorSocket;
import se.de.hu_berlin.informatik.utils.tm.user.ProcessorSocket;

/**
 * A {@link ProcessorSocket} implementation that provides basic functionalities
 * of a modular framework. Modules have a single input 
 * and an output object at any point in time and can be linked together 
 * such that one Module provides the input to another Module. 
 * 
 * <br><br> For convenience, multiple (matching) Modules may be linked together 
 * like this:
 * 
 * <br><br> {@code module1.linkTo(module2).linkTo(module3).linkTo(...)...;}
 * 
 * <br><br> which will link the output of {@code module1} to the input of 
 * {@code module2} and then link the output of {@code module2} to the input of
 * {@code module3}, etc.
 * 
 * <br><br> After linking, any matching item submitted to the first Module
 * will start the execution process. The end result can be obtained from the
 * last linked Module via the {@code getResult()} method. In theory, one could
 * also obtain the intermediate results from each of the linked Modules if
 * needed.
 * 
 * <br><br> In general, Modules should not be linked manually and should
 * preferably be linked together with a {@link ModuleLinker} which provides
 * more general and easier access methods.
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
public class Module<A,B> extends AbstractProcessorSocket<A,B> {
	
	private B output = null;
	
	private Module<B,?> linkedModule = null;
	
	/**
	 * Creates a new module with the given parameter.
	 * @param processor
	 * the processor to use
	 */
	public Module(Processor<A,B> processor) {
		super(processor);
	}
	
	@Override
	public <C> ProcessorSocket<C,?> linkTo(ProcessorSocket<C,?> consumer) throws IllegalArgumentException, IllegalStateException {
		if (consumer instanceof Module) {
			return linkModuleTo((Module<C, ?>)consumer);
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
	private <C> Module<C,?> linkModuleTo(Module<C,?> module) throws IllegalArgumentException {
		try {
			this.linkedModule = (Module<B,?>) module;
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
	public Module<A,B> submit(Object item) {
		if (item == null) {
			Log.err(this, "No input item submitted/available.");
		} else {
			try {
				initAndConsume((A)item);
			} catch (ClassCastException e) {
				Log.abort(this, e, "Type mismatch while submitting!");
			}
//			if (linkedModule != null) {
//				linkedModule.submit(output);
//			}
		}
		return this;
	}
	
	@Override
	public void produce(B item) {
		output = item;
		if (linkedModule != null) {
			linkedModule.submit(item);
		}
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
	public Module<B,?> getLinkedModule() {
		if (linkedModule == null) {
			Log.abort(this, "No module linked to.");
		}
		return linkedModule;
	}

	@Override
	public Pipe<A, B> asPipe() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("not supported");
	}

	@Override
	public Module<A, B> asModule() throws UnsupportedOperationException {
		return this;
	}

	@Override
	public <E extends AbstractDisruptorEventHandler<A> & ProcessorSocket<A,B>> E asEH() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("not supported");
	}
	
}
