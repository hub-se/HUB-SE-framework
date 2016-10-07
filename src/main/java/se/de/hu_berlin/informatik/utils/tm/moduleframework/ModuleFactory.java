package se.de.hu_berlin.informatik.utils.tm.moduleframework;

public abstract class ModuleFactory<A,B> {
	
	AModule<A,B> module = null;

	/**
	 * Tries to get or create a module.
	 * @return
	 * a module, if possible
	 * @throws IllegalStateException
	 * if not possible
	 */
	public AModule<A,B> getModule() throws IllegalStateException {
		if (module == null) {
			module = newModule();
		}
		return module;
	}
	
	/**
	 * Tries to create a module.
	 * @return
	 * a module, if possible
	 * @throws IllegalStateException
	 * if not possible
	 */
	abstract public AModule<A,B> newModule() throws IllegalStateException;
	
}
