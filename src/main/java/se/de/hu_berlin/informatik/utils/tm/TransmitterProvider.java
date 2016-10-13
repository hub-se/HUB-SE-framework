package se.de.hu_berlin.informatik.utils.tm;

import se.de.hu_berlin.informatik.utils.tm.moduleframework.AbstractModule;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AbstractModuleFactory;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipe;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipeFactory;

public interface TransmitterProvider<A,B> {

	/**
	 * May return instances of a pipe. Returns null by default.
	 * @return
	 * a pipe factory, or null if none is specified
	 */
	default public AbstractPipeFactory<A,B> getPipeProvider() {
		return null;
	}
	
	/**
	 * May return instances of a module. Returns null by default.
	 * @return
	 * a module factory, or null if none is specified
	 */
	default public AbstractModuleFactory<A,B> getModuleProvider() {
		return null;
	}
	
	/**
	 * Tries to get or create a pipe. Tries to use the pipe factory, if
	 * specified, by default.
	 * @return
	 * a pipe, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	default public AbstractPipe<A,B> asPipe() throws UnsupportedOperationException {
		if (getPipeProvider() == null) {
			throw new UnsupportedOperationException("No pipe provider given.");
		}
		return getPipeProvider().getPipe();
	}
	
	/**
	 * Tries to get or create a module. Tries to use the module factory, if
	 * specified, by default.
	 * @return
	 * a module, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	default public AbstractModule<A,B> asModule() throws UnsupportedOperationException {
		if (getModuleProvider() == null) {
			throw new UnsupportedOperationException("No module provider given.");
		}
		return getModuleProvider().getModule();
	}
}
