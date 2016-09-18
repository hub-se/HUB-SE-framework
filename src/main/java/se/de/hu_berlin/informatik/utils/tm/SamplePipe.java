/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.APipe;

/**
 * Sample pipe for convenience. (Copy and implement the needed functions)
 * 
 * @author Simon Heiden
 *
 * @param <A>
 * is the type of the input objects
 * @param <B>
 * is the type of the output objects
 */
public class SamplePipe<A,B> extends APipe<A,B> {

	// TODO create needed fields
	
	public SamplePipe() {
		super(false);
		// TODO initialize needed fields, etc.
	}

	public SamplePipe(int pipeSize) {
		super(pipeSize, false);
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
