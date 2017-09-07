/**
 * 
 */
package se.de.hu_berlin.informatik.utils.processors.sockets.module;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.optionparser.OptionCarrier;
import se.de.hu_berlin.informatik.utils.optionparser.OptionParser;
import se.de.hu_berlin.informatik.utils.processors.sockets.ProcessorSocketGenerator;
import se.de.hu_berlin.informatik.utils.tracking.Trackable;
import se.de.hu_berlin.informatik.utils.tracking.TrackingStrategy;
import se.de.hu_berlin.informatik.utils.tracking.TrackerDummy;

/**
 * Provides more general and easy access methods for the linking of Modules,
 * for the submission of items to a chain of Modules and for obtaining
 * result items.
 * 
 * @author Simon Heiden
 *
 * @see Module
 */
public class ModuleLinker implements Trackable, OptionCarrier {

	private Module<?,?> startModule = null;
	private Module<?,?> endModule = null;
	private OptionParser options = null;
	
	public ModuleLinker() {
		super();
	}
	
	/**
	 * @param options
	 * an options object to distribute to the modules in this linker
	 */
	public ModuleLinker(OptionParser options) {
		this();
		this.options = options;
	}

	/**
	 * Links the given Modules (provided by socket generators, possibly)
	 * together and appends them to former appended 
	 * Modules, if any. If the Modules don't match, then
	 * execution stops and the application aborts.
	 * @param generators
	 * Modules to be linked together (given as generators, possibly)
	 * @return
	 * this ModuleLinker
	 */
	public ModuleLinker append(ProcessorSocketGenerator<?,?>... generators) {
		if (generators.length != 0) {
			try {
				generators[0].asModule().setOptions(options);
				if (startModule == null) {
					startModule = generators[0].asModule();
					if (isTracking()) {
						startModule.enableTracking(getTracker());
					}
				} else {
					endModule.linkTo(generators[0].asModule());
				}
				for (int i = 0; i < generators.length-1; ++i) {
					generators[i].asModule().linkTo(generators[i+1].asModule());
					generators[i+1].asModule().setOptions(options);
				}
				endModule = generators[generators.length-1].asModule();
			} catch(UnsupportedOperationException e) {
				Log.abort(this, e, "Unable to get module from a given transmitter.");
			}
		}
		return this;
	}

	/**
	 * @return 
	 * the start module
	 */
	private Module<?, ?> getStartModule() {
		if (startModule == null) {
			Log.abort(this, "No start module available.");
		}
		return startModule;
	}

	/**
	 * @return 
	 * the end module
	 */
	private Module<?, ?> getEndModule() {
		if (endModule == null) {
			Log.abort(this, "No end module available.");
		}
		return endModule;
	}

	/**
	 * Submits a single or multiple items to the underlying chain
	 * of modules.
	 * @param items
	 * items to be submitted
	 * @return
	 * this module linker
	 */
	public ModuleLinker submit(Object... items) {
		for (int i = 0; i < items.length; ++i) {
			getStartModule().submit(items[i]);
		}
		return this;
	}
	
	/**
	 * @return
	 * the result item of the last module in the chain, if any
	 */
	public Object getLastResult() {
		return getEndModule().getResult();
	}
	
	/**
	 * @return
	 * the collected result item of the last module in the chain, if any
	 */
	public Object getCollectedResult() {
		return getEndModule().getResultFromCollectedItems();
	}
	
	@Override
	public ModuleLinker enableTracking() {
		if (startModule != null) {
			startModule.enableTracking();
		}
		return this;
	}
	
	@Override
	public ModuleLinker enableTracking(int stepWidth) {
		if (startModule != null) {
			startModule.enableTracking(stepWidth);
		}
		return this;
	}

	@Override
	public ModuleLinker disableTracking() {
		if (startModule != null) {
			startModule.disableTracking();
		}
		return this;
	}

	@Override
	public ModuleLinker enableTracking(TrackingStrategy tracker) {
		if (startModule != null) {
			startModule.enableTracking(tracker);
		}
		return this;
	}

	@Override
	public ModuleLinker enableTracking(boolean useProgressBar) {
		if (startModule != null) {
			startModule.enableTracking(useProgressBar);
		}
		return this;
	}

	@Override
	public ModuleLinker enableTracking(boolean useProgressBar, int stepWidth) {
		if (startModule != null) {
			startModule.enableTracking(useProgressBar, stepWidth);
		}
		return this;
	}

	@Override
	public boolean isTracking() {
		if (startModule != null) {
			return startModule.isTracking();
		}
		return false;
	}

	@Override
	public void track() {
		if (startModule != null) {
			startModule.track();
		}
	}

	@Override
	public void track(String msg) {
		if (startModule != null) {
			startModule.track(msg);
		}
	}

	@Override
	public void delegateTrackingTo(Trackable target) {
		if (startModule != null) {
			startModule.delegateTrackingTo(target);
		}
	}

	@Override
	public TrackingStrategy getTracker() {
		if (startModule != null) {
			return startModule.getTracker();
		}
		return TrackerDummy.getInstance();
	}

	@Override
	public void setTracker(TrackingStrategy tracker) {
		if (startModule != null) {
			startModule.setTracker(tracker);
		}
	}

	@Override
	public boolean onlyForced() {
		if (startModule != null) {
			return startModule.onlyForced();
		} else {
			return false;
		}
	}

	@Override
	public ModuleLinker allowOnlyForcedTracks() {
		if (startModule != null) {
			startModule.allowOnlyForcedTracks();
		}
		return this;
	}

	@Override
	public OptionParser getOptions() {
		return options;
	}

	@Override
	public ModuleLinker setOptions(OptionParser options) {
		this.options = options;
		return this;
	}
	
	@Override
	public boolean hasOptions() {
		return options != null;
	}
	
}
