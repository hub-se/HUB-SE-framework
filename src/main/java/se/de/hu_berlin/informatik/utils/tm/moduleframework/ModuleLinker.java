/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.moduleframework;

import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;

/**
 * Provides more general and easy access methods for the linking of modules,
 * for the submission of items to a chain of modules and for obtaining
 * result items.
 * 
 * @author Simon Heiden
 *
 * @see AModule
 */
public class ModuleLinker {

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
		}
		return this;
	}
	
	/**
	 * @return 
	 * the start module
	 */
	private AModule<?, ?> getStartModule() {
		if (startModule == null) {
			Misc.abort(this, "No start module available.");
		}
		return startModule;
	}

	/**
	 * @return 
	 * the end module
	 */
	private AModule<?, ?> getEndModule() {
		if (endModule == null) {
			Misc.abort(this, "No end module available.");
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
	public ModuleLinker submitAndStart(Object... items) {
		for (int i = 0; i < items.length; ++i) {
			getStartModule().submitAndStart(items[i]);
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
}