/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.user;

import se.de.hu_berlin.informatik.utils.optionparser.OptionCarrier;
import se.de.hu_berlin.informatik.utils.optionparser.OptionParser;
import se.de.hu_berlin.informatik.utils.tm.Processor;
import se.de.hu_berlin.informatik.utils.tm.Producer;
import se.de.hu_berlin.informatik.utils.tracking.Trackable;
import se.de.hu_berlin.informatik.utils.tracking.TrackingStrategy;

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
public interface ProcessorUser<A,B> extends Producer<B>, ProcessorUserGenerator<A, B>, Trackable, OptionCarrier {
	
	public void setProcessor(Processor<A, B> processor);
	
	public Processor<A, B> getProcessor() throws IllegalStateException;

	/**
	 * Resets and initializes the processor, and then
	 * processes the given item.
	 * @param item
	 * the item to process
	 */
	default void initAndConsume(A item) {
		getProcessor().resetAndInit();
		getProcessor().trackAndConsume(item);
	}
	
	/**
	 * @return
	 * the result of unprocessed collected items
	 */
	default public B getResultFromCollectedItems(){
		return getProcessor().getResultFromCollectedItems();
	}
	
	/**
	 * Should cut all loose ends.
	 * @return
	 * true if successful
	 */
	default public boolean finalShutdown() {
		return getProcessor().finalShutdown();
	}
	
	
	@Override
	default public ProcessorUser<A,B> enableTracking() {
		getProcessor().enableTracking();
		return this;
	}

	@Override
	default public ProcessorUser<A,B> enableTracking(int stepWidth) {
		getProcessor().enableTracking(stepWidth);
		return this;
	}

	@Override
	default public ProcessorUser<A,B> disableTracking() {
		getProcessor().disableTracking();
		return this;
	}

	@Override
	default public ProcessorUser<A,B> enableTracking(TrackingStrategy tracker) {
		getProcessor().enableTracking(tracker);
		return this;
	}

	@Override
	default public ProcessorUser<A,B> enableTracking(boolean useProgressBar) {
		getProcessor().enableTracking(useProgressBar);
		return this;
	}

	@Override
	default public ProcessorUser<A,B> enableTracking(boolean useProgressBar, int stepWidth) {
		getProcessor().enableTracking(useProgressBar, stepWidth);
		return this;
	}
	
	@Override
	default public TrackingStrategy getTracker() {
		return getProcessor().getTracker();
	}

	@Override
	default public void setTracker(TrackingStrategy tracker) {
		getProcessor().setTracker(tracker);
	}
	
	@Override
	default public boolean onlyForced() {
		return getProcessor().onlyForced();
	}

	@Override
	default public void allowOnlyForcedTracks() {
		getProcessor().allowOnlyForcedTracks();
	}
	
	@Override
	default public OptionParser getOptions() {
		return getProcessor().getOptions();
	}

	@Override
	default public ProcessorUser<A,B> setOptions(OptionParser options) {
		getProcessor().setOptions(options);
		return this;
	}
	
	@Override
	default public boolean hasOptions() {
		return getProcessor().hasOptions();
	}
	
}
