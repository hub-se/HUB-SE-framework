package se.de.hu_berlin.informatik.utils.threaded;

public interface IMultiplexer<B> extends Runnable {

	/**
	 * Sets the connected handlers that generate the output items that
	 * shall be collected by this multiplexer. 
	 * @param handlers
	 * the handlers
	 */
	public void connectHandlers(IMultiplexerInput<B>[] handlers);

	/**
	 * Creates a new multiplexer thread if not already a running thread exists.
	 * Then starts the created thread and returns it.
	 * @return
	 * the started thread
	 */
	public Thread start();
	
	/**
	 * @return
	 * this multiplexer's thread
	 */
	public Thread getThread();

	/**
	 * Shuts down the multiplexer thread. Waits until the thread
	 * has collected all pending output items.
	 */
	public void shutdown();
	
	/**
	 * Acquires the handler's lock and notifies him that its output 
	 * has been collected, since it might wait, having already 
	 * generated new output...
	 * @param handler
	 * the handler to notify
	 */
	default public void notifyHandler(IMultiplexerInput<B> handler) {
		synchronized (handler.getLock()) {
			handler.getLock().notify();
		}
	}

	/**
	 * Pauses this multiplexer's thread until a check for pending items
	 * is being intiated.
	 */
	public void waitForNotifications();
	
	/**
	 * Starts to check for new output items being available to collect.
	 */
	public void initiateCheckForPendingItems();
	
	/**
	 * Checks for any pending items and processes each item that is valid.
	 * @param handlers
	 * the connected handlers
	 */
	default public void checkForPendingItems(IMultiplexerInput<B>[] handlers) {
		for (int i = 0; i < handlers.length; ++i) {
			//get the output (will be null if not new)
			B result = handlers[i].getOutputAndInvalidate();
			if (result == null) {
				//try the next handler
				continue;
			}
			//process items that are not null
			processNewOutputItem(result);
			//notify the handler that the output has been collected
			notifyHandler(handlers[i]);
		}
	}
	
	/**
	 * Processes an item of type B. Has to be implemented by any
	 * class implementing this interface.
	 * @param item
	 * the item to process
	 */
	public void processNewOutputItem(B item);
	
}
