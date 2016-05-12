/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm;

/**
 * An interface that provides basic functionalities of transmitters that can be linked together.
 * 
 * @author Simon Heiden
 *
 * @param <A>
 * is the type of the input object
 * @param <B>
 * is the type of the output object
 */
public interface ITransmitter<A,B> {
	
	/**
	 * Processes an item of type {@code A} and produces an item of type {@code B}.
	 * @param item
	 * the item to be processed
	 * @return
	 * the processed item
	 */
	public B processItem(A item);
	
	/**
	 * Links a matching transmitter to the output of this transmitter.
	 * @param <C>
	 * the input type of the transmitter to be linked to
	 * @param <D>
	 * the output type of the transmitter to be linked to
	 * @param transmitter
	 * the transmitter to be linked to
	 * @return
	 * the transmitter to be linked to
	 */
	public <C,D> ITransmitter<C,D> linkTo(ITransmitter<C,D> transmitter);
	
	/**
	 * Should be overwritten by implementing transmitters that may collect
	 * input items without immediately processing them. This method should
	 * process possibly remaining collected items and/or return the result
	 * (or null if there is no result).
	 * @return
	 * the result of unprocessed collected items
	 */
	default public B getResultFromCollectedItems(){
		return null;
	}
	
	/**
	 * Should cut all loose ends.
	 * @return
	 * true if successful
	 */
	default public boolean finalShutdown() {
		return true;
	}

}
