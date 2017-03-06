/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.user;

import se.de.hu_berlin.informatik.utils.tm.Processor;
import se.de.hu_berlin.informatik.utils.tm.Producer;

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
public interface ProcessorUser<A,B> extends Producer<B>, ProcessorUserGenerator<A, B> {
	
	public void setProcessor(Processor<A, B> processor);
	
	public Processor<A, B> getProcessor() throws IllegalStateException;

	default void consume(A item) {
		getProcessor().consume(item);
	}
	
	/**
	 * Should cut all loose ends.
	 * @return
	 * true if successful
	 */
	default public boolean finalShutdown() {
		return getProcessor().finalShutdown();
	}
	
}
