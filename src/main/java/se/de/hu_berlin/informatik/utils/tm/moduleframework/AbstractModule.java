/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.moduleframework;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.optionparser.OptionParser;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInputAndReturn;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInputAndReturnFactory;
import se.de.hu_berlin.informatik.utils.tm.Transmitter;
import se.de.hu_berlin.informatik.utils.tm.TransmitterProvider;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.ModuleLinker;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipe;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipeFactory;
import se.de.hu_berlin.informatik.utils.tracking.Trackable;
import se.de.hu_berlin.informatik.utils.tracking.TrackingStrategy;
import se.de.hu_berlin.informatik.utils.tracking.TrackerDummy;

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
public abstract class AbstractModule<A,B> implements Transmitter<A,B>, TransmitterProvider<A,B>, Trackable {
	
	private B output = null;
	
	private AbstractModule<B,?> linkedModule = null;
	
	private boolean needsInput = false;
	private TrackingStrategy tracker = TrackerDummy.getInstance();
	
	private AbstractModuleFactory<A,B> moduleProvider = null;
	private AbstractPipeFactory<A,B> pipeProvider = null;
	private EHWithInputAndReturnFactory<A,B> ehProvider = null;

	private boolean onlyForced = false;

	private OptionParser options = null;
	
	/**
	 * Creates a new module with the given parameter.
	 * @param needsInput
	 * determines if the module needs an input item to function
	 */
	public AbstractModule(boolean needsInput) {
		super();
		this.needsInput = needsInput;
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.miscellaneous.ITransmitter#linkTo(se.de.hu_berlin.informatik.utils.miscellaneous.ITransmitter)
	 */
	@Override
	public <C, D> Transmitter<C, D> linkTo(Transmitter<C, D> transmitter) throws IllegalArgumentException, IllegalStateException {
		if (transmitter instanceof AbstractModule) {
			return linkModuleTo((AbstractModule<C, D>)transmitter);
		} else {
			throw new IllegalStateException("Can only link to other modules.");
		}
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
	 * @throws IllegalArgumentException
	 * if the input type C of the given module does not match the output type B of this module
	 */
	@SuppressWarnings("unchecked")
	private <C,D> AbstractModule<C,D> linkModuleTo(AbstractModule<C,D> module) throws IllegalArgumentException {
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
		try {
			process((A)item);
		} catch (ClassCastException e) {
			Log.abort(this, e, "Type mismatch while submitting!");
		}
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

	/**
	 * Processes an available input item (if any) and sets the result as
	 * the output item of this module.
	 * @param input
	 * the input item (may be null)
	 */
	private void process(A input) {
		if (needsInput && input == null) {
			Log.err(this, "No input item submitted/available.");
			return;
		}
		track();
		output = processItem(input);
	}

	@Override
	public AbstractModule<A,B> enableTracking() {
		Trackable.super.enableTracking();
		return this;
	}
	
	@Override
	public AbstractModule<A,B> enableTracking(int stepWidth) {
		Trackable.super.enableTracking(stepWidth);
		return this;
	}

	@Override
	public AbstractModule<A,B> disableTracking() {
		Trackable.super.disableTracking();
		return this;
	}

	@Override
	public AbstractModule<A,B> enableTracking(TrackingStrategy tracker) {
		Trackable.super.enableTracking(tracker);
		return this;
	}

	@Override
	public AbstractModule<A,B> enableTracking(boolean useProgressBar) {
		Trackable.super.enableTracking(useProgressBar);
		return this;
	}

	@Override
	public AbstractModule<A,B> enableTracking(boolean useProgressBar, int stepWidth) {
		Trackable.super.enableTracking(useProgressBar, stepWidth);
		return this;
	}

	@Override
	public AbstractModuleFactory<A, B> getModuleProvider() {
		if (moduleProvider == null) {
			moduleProvider = new AbstractModuleFactory<A,B>() {
				@Override
				public AbstractModule<A, B> getModule() {
					//simply return the actual module
					return AbstractModule.this;
				}
				@Override
				public AbstractModule<A, B> newModule() throws UnsupportedOperationException {
					//should not be accessed
					throw new UnsupportedOperationException("Trying to create new module when one already exists.");
				}
			};
		}
		return moduleProvider;
	}
	
	@Override
	public AbstractPipeFactory<A, B> getPipeProvider() {
		if (pipeProvider == null) {
			pipeProvider = new AbstractPipeFactory<A,B>() {
				@Override
				public AbstractPipe<A, B> newPipe() {
					return new AbstractPipe<A,B>(true) {
						@Override
						public B processItem(A item) {
							return AbstractModule.this.processItem(item);
						}
					};
				}
			};
		}
		return pipeProvider;
	}

	@Override
	public EHWithInputAndReturnFactory<A, B> getEHProvider() {
		if (ehProvider == null) {
			ehProvider = new EHWithInputAndReturnFactory<A,B>() {
				@Override
				public EHWithInputAndReturn<A, B> newFreshInstance() {
					return new EHWithInputAndReturn<A,B>() {
						@Override
						public B processInput(A input) {
							return AbstractModule.this.processItem(input);
						}
						@Override
						public void resetAndInit() { /*do nothing*/ }
					};
				}
			};
		}
		return ehProvider;
	}

	@Override
	public TrackingStrategy getTracker() {
		return tracker;
	}

	@Override
	public void setTracker(TrackingStrategy tracker) {
		this.tracker = tracker;
	}
	
	@Override
	public boolean onlyForced() {
		return onlyForced;
	}

	@Override
	public void allowOnlyForcedTracks() {
		onlyForced = true;
	}

	@Override
	public OptionParser getOptions() {
		return options;
	}

	@Override
	public AbstractModule<A, B> setOptions(OptionParser options) {
		this.options = options;
		return this;
	}
	
	@Override
	public boolean hasOptions() {
		return options != null;
	}
	
}
