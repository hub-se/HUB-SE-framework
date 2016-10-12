/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.moduleframework;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.tm.ITransmitterProvider;
import se.de.hu_berlin.informatik.utils.tracking.ITrackable;
import se.de.hu_berlin.informatik.utils.tracking.ITrackingStrategy;
import se.de.hu_berlin.informatik.utils.tracking.TrackerDummy;

/**
 * Provides more general and easy access methods for the linking of modules,
 * for the submission of items to a chain of modules and for obtaining
 * result items.
 * 
 * @author Simon Heiden
 *
 * @see AModule
 */
public class ModuleLinker implements ITrackable {

	private AModule<?,?> startModule = null;
	private AModule<?,?> endModule = null;
	
	/**
	 * Links the given modules together and appends them to former appended 
	 * modules, if any. If the modules don't match, then
	 * execution stops and the application aborts.
	 * @param transmitters
	 * modules to be linked together
	 * @return
	 * this module linker
	 */
	public ModuleLinker append(ITransmitterProvider<?,?>... transmitters) {
		if (transmitters.length != 0) {
			try {
				if (startModule == null) {
					startModule = transmitters[0].asModule();
					if (isTracking()) {
						startModule.enableTracking(getTracker());
					}
				} else {
					endModule.linkTo(transmitters[0].asModule());
				}
				for (int i = 0; i < transmitters.length-1; ++i) {
					transmitters[i].asModule().linkTo(transmitters[i+1].asModule());
				}
				endModule = transmitters[transmitters.length-1].asModule();
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
	private AModule<?, ?> getStartModule() {
		if (startModule == null) {
			Log.abort(this, "No start module available.");
		}
		return startModule;
	}

	/**
	 * @return 
	 * the end module
	 */
	private AModule<?, ?> getEndModule() {
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
	 * Starts processing without an input item.
	 * @return
	 * this module linker
	 */
	public ModuleLinker start() {
		getStartModule().start();
		return this;
	}
	
	/**
	 * @return
	 * the result item of the last module in the chain
	 */
	public Object getLastResult() {
		return getEndModule().getResult();
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
	public ModuleLinker enableTracking(ITrackingStrategy tracker) {
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
			startModule.isTracking();
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
	public void delegateTrackingTo(ITrackable target) {
		if (startModule != null) {
			startModule.delegateTrackingTo(target);
		}
	}

	@Override
	public ITrackingStrategy getTracker() {
		if (startModule != null) {
			return startModule.getTracker();
		}
		return TrackerDummy.getInstance();
	}

	@Override
	public void setTracker(ITrackingStrategy tracker) {
		if (startModule != null) {
			startModule.setTracker(tracker);
		}
	}
}
