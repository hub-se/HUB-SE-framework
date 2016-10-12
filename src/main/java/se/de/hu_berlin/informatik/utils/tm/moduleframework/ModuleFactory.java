package se.de.hu_berlin.informatik.utils.tm.moduleframework;

public abstract class ModuleFactory<A,B> {
	
	AModule<A,B> module = null;

	/**
	 * Tries to get or create a module.
	 * @return
	 * a module, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	public AModule<A,B> getModule() throws UnsupportedOperationException {
		if (module == null) {
			module = newModule();
		}
		return module;
	}
	
	/**
	 * Tries to create a module.
	 * @return
	 * a module, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	abstract public AModule<A,B> newModule() throws UnsupportedOperationException;
	
}
