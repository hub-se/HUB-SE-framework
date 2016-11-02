package se.de.hu_berlin.informatik.utils.threaded.disruptor;

public interface Multiplexer<B> extends Runnable {

	/**
	 * Sets the connected handlers that generate the output items that
	 * shall be collected by this multiplexer. 
	 * @param handlers
	 * the handlers
	 */
	public void connectHandlers(MultiplexerInput<B>[] handlers);

	/**
	 * Creates a new multiplexer thread if a running thread does not exists.
	 * Then starts the created thread.
	 */
	public void start();
	
	/**
	 * @return
	 * whether this multiplexer's thread is running
	 */
	public boolean isRunning();

	/**
	 * Shuts down the multiplexer thread. Waits until the thread
	 * has collected all pending output items.
	 */
	public void shutdown();
	
	/**
	 * Checks for any pending items and processes each item that is valid.
	 * @param handlers
	 * the connected handlers
	 */
	default public void processPendingItems(MultiplexerInput<B>[] handlers) {
		for (int i = 0; i < handlers.length; ++i) {
			//check for validity
			if (handlers[i].outputItemIsValid()) {
				//get the output and notify the handler
				B result = handlers[i].getValidOutputAndNotifyHandler();
				//process valid item
				processNewOutputItem(result);
			}
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
