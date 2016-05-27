/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.modules.stringprocessor;

import se.de.hu_berlin.informatik.utils.fileoperations.FileLineProcessorModule;

/**
 * Provides a method that gets a {@link String} and processes
 * it in some way. Also provides a method to obtain a result
 * object in the end. Instances of implementing classes can
 * be loaded into a {@link FileLineProcessorModule}, for example.
 * 
 * @author Simon Heiden
 *
 */
public interface IStringProcessor {

	/**
	 * Takes a {@link String} and processes it in some way.
	 * @param item
	 * an input {@link String}
	 * @return
	 * true if the operation succeeded, false otherwise
	 */
	public boolean process(String item);
	
	/**
	 * @return
	 * the result of the processing. May be anything and has to be 
	 * used appropriately by calling classes
	 */
	public Object getResult();

	/**
	 * Should be overwritten by implementing String processors that may collect
	 * input items without immediately processing them. This method should
	 * process possibly remaining collected items and/or return the result
	 * (or null if there is no result).
	 * @return
	 * the result of unprocessed collected items
	 */
	default public Object getResultFromCollectedItems(){
		return null;
	}
}
