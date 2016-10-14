package se.de.hu_berlin.informatik.utils.threaded.disruptor;

/**
 * Provides an interface for a multiplexer to obtain new outputs of an
 * underlying thread with all necessary access methods.
 * 
 * @author Simon
 *
 * @param <B>
 * the type of output items
 */
public interface MultiplexerInput<B> {

	public Object getLock();
	
	/**
	 * Returns the current output item. May be valid or invalid or {@code null}.
	 * @return
	 * the current, available output item
	 */
	public B getOutput();
	
	/**
	 * Returns available, new output items or {@code null}, otherwise. 
	 * Invalidates the current output such that it can be replaced by newly
	 * generated output items in the future.
	 * @return
	 * a new output item which shall be processed by the multiplexer, or
	 * {@code null}, otherwise
	 */
	default public B getOutputAndInvalidate() {
		if (outputItemIsValid()) {
			B temp = getOutput();
			setOutputItemInvalid();
			return temp;
		} else {
			return null;
		}
	}
	
	/**
	 * Sets a new output item. Assumes the item is not {@code null} and
	 * that old output items have already been collected by the multiplexer.
	 * @param item
	 * the item to be submitted
	 * @return
	 * true if successful, false otherwise
	 */
	public boolean setOutput(B item);
	
	/**
	 * Waits for old output items to be collected by the multiplexer.
	 */
	default public void waitForOldOutputToBeCollected() {
		//only acquire lock if it's necessary
		if (outputItemIsValid()) {
			synchronized (getLock()) {
				while (outputItemIsValid()) {
					try {
						getLock().wait();
					} catch (InterruptedException e) {
						// do nothing
					}
				}
			}
		}
	}
	
	/**
	 * @return
	 * whether the output item is valid (i.e. it has not yet been
	 * collected by the multiplexer)
	 */
	public boolean outputItemIsValid();
	
	/**
	 * Marks the current output item as valid.
	 */
	public void setOutputItemValid();
	
	/**
	 * Marks the current output item as invalid.
	 */
	public void setOutputItemInvalid();
	
	/**
	 * Waits for old output items to be collected (if any). Then sets a
	 * a new output item and notifies the multiplexer if successful.
	 * If the given item is {@code null}, then this method has no effect.
	 * @param item
	 * a new output item (may be {@code null})
	 */
	default public void setOutputAndNotifyMultiplexer(B item) {
		if (item != null) {
			waitForOldOutputToBeCollected();
			if(setOutput(item)) {
				setOutputItemValid();
				notifyMultiplexer();
			}
		}
	}
	
	/**
	 * Sets a reference to a multiplexer.
	 * @param multiplexer
	 * the multiplexer
	 */
	public void setMultiplexer(Multiplexer<B> multiplexer);
	
	/**
	 * @return
	 * the associated multiplexer
	 */
	public Multiplexer<B> getMultiplexer();
	
	/**
	 * Notifies the connected multiplexer about new output items being available.
	 */
	default public void notifyMultiplexer() {
		getMultiplexer().initiateCheckForPendingItems();
	}
	
}
