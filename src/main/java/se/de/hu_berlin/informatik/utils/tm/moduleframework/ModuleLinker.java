/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.moduleframework;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.tracking.ProgressTracker;
import se.de.hu_berlin.informatik.utils.tracking.Trackable;

/**
 * Provides more general and easy access methods for the linking of modules,
 * for the submission of items to a chain of modules and for obtaining
 * result items.
 * 
 * @author Simon Heiden
 *
 * @see AModule
 */
public class ModuleLinker extends Trackable {

	private AModule<?,?> startModule = null;
	private AModule<?,?> endModule = null;
	
	/**
	 * Links the given modules together. If the modules don't match, then
	 * execution stops and the application aborts.
	 * @param modules
	 * modules to be linked together
	 * @return
	 * this module linker
	 */
	public ModuleLinker link(AModule<?,?>... modules) {
		if (modules.length != 0) {
			startModule = modules[0];
			for (int i = 0; i < modules.length-1; ++i) {
				modules[i].linkTo(modules[i+1]);
			}
			endModule = modules[modules.length-1];
			
			if (isTracking()) {
				startModule.enableTracking(getTracker());
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
		super.enableTracking();
		if (startModule != null) {
			startModule.enableTracking(this.getTracker());
		}
		return this;
	}
	
	@Override
	public ModuleLinker enableTracking(int stepWidth) {
		super.enableTracking(stepWidth);
		if (startModule != null) {
			startModule.enableTracking(this.getTracker());
		}
		return this;
	}

	@Override
	public ModuleLinker disableTracking() {
		super.disableTracking();
		if (startModule != null) {
			startModule.disableTracking();
		}
		return this;
	}

	@Override
	public ModuleLinker enableTracking(ProgressTracker tracker) {
		super.enableTracking(tracker);
		if (startModule != null) {
			startModule.enableTracking(this.getTracker());
		}
		return this;
	}
}
