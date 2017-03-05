package se.de.hu_berlin.informatik.utils.tm.moduleframework;

@Deprecated
public abstract class AbstractModuleFactory<A,B> {
	
	AbstractModule<A,B> module = null;

	/**
	 * Tries to get or create a module.
	 * @return
	 * a module, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	public AbstractModule<A,B> getModule() throws UnsupportedOperationException {
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
	abstract public AbstractModule<A,B> newModule() throws UnsupportedOperationException;
	
}
