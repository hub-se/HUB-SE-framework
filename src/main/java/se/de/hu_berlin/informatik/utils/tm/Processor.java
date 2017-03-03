/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm;

import se.de.hu_berlin.informatik.utils.optionparser.OptionCarrier;

/**
 * An interface that provides basic functionalities of a processor.
 * 
 * @author Simon Heiden
 *
 * @param <A>
 * is the type of the input object
 * @param <B>
 * is the type of the output object
 */
public interface Processor<A,B> extends OptionCarrier {
	
	/**
	 * Processes an item of type {@code A} and produces an item of type {@code B}.
	 * @param item
	 * the item to be processed
	 * @return
	 * the processed item
	 */
	public B process(A item);
	
//	/**
//	 * Should be overwritten by implementing processors that may collect
//	 * input items without immediately processing them. This method should
//	 * process possibly remaining collected items and/or return the result
//	 * (or null if there is no result).
//	 * @return
//	 * the result of unprocessed collected items
//	 */
//	default public B getResultFromCollectedItems(){
//		return null;
//	}

}
