/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.pipes;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AbstractModule;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipe;

/**
 * Convenience class that is able to load a module into a pipe object.
 * 
 * @author Simon Heiden
 * 
 * @param <A>
 * is the type of the input objects
 * @param <B>
 * is the type of the output objects
 * 
 * @see AbstractModule
 */
public class ModuleLoaderPipe<A,B> extends AbstractPipe<A,B> {

	private AbstractModule<A,B> module = null;
	
	/**
	 * Creates a new {@link ModuleLoaderPipe} object and loads the
	 * given module.
	 * @param module
	 * the module to load
	 */
	public ModuleLoaderPipe(AbstractModule<A, B> module) {
		super(true);
		this.module = module;
	}

	/**
	 * Creates a new {@link ModuleLoaderPipe} object with the
	 * specified pipe size and loads the given module.
	 * @param module
	 * the module to load
	 * @param pipeSize
	 * the size of the output pipe
	 */
	public ModuleLoaderPipe(AbstractModule<A, B> module, int pipeSize) {
		super(pipeSize, true);
		this.module = module;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public B processItem(A item) {		
		return getInternalModule().submit(item).getResult();
	}
	
	/**
	 * Loads a matching module into the pipe.
	 * @param module
	 * the module to load
	 */
	public void loadModule(AbstractModule<A,B> module) {
		this.module = module;
	}
	
	/**
	 * Returns the loaded module, if any, or aborts the application
	 * with an error message otherwise.
	 * @return
	 * the loaded module
	 */
	private AbstractModule<A, B> getInternalModule() {
		if (module == null) {
			Log.abort(this, "No module loaded.");
		}
		return module;
	}

	/**
	 * Returns possible remaining results from the loaded module.
	 */
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#getResultFromCollectedItems()
	 */
	@Override
	public B getResultFromCollectedItems() {
		return getInternalModule().getResultFromCollectedItems();
	}

	@Override
	public boolean finalShutdown() {
		return getInternalModule().finalShutdown();
	}	

}
