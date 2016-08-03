/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AModule;

/**
 * Sample module for convenience. (Copy and implement the needed functions)
 * 
 * @author Simon Heiden
 * 
 * @param <A>
 * is the type of the input object
 * @param <B>
 * is the type of the output object
 */
public class SampleModule<A,B> extends AModule<A,B> {

	// TODO create needed fields
	
	public SampleModule() {
		//if this module needs an input item
		super(true);
		//if it doesn't need an item use
		//super();
		// TODO initialize needed fields, etc.
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public B processItem(A item) {
		// TODO implement useful function that produces an object of type B
		Log.abort(this, "Nothing useful to do here...");
		//return (B)item;
		return null;
	}

}
