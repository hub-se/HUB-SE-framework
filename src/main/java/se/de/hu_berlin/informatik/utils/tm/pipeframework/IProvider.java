/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.pipeframework;

/**
 * Provides an interface for a simple provider which can
 * be used to submit items to and to get items from.
 * Additionally, methods are provided to check and alter
 * the provider's working status.
 * 
 * @author Simon Heiden
 *
 * @param <A>
 * type of the items that the provider may hold
 * @param <V>
 * type of the objects that are connected to the provider
 */
public interface IProvider<A,V> {
	
	/**
	 * Submits an item to the provider.
	 * @param item
	 * the item to be submitted
	 */
	public void submit(Object item);
	
	/**
	 * Gets an item from the provider.
	 * @return
	 * the item
	 */
	public A get();

	/**
	 * @return
	 * if the provider is done
	 */
	public boolean isProviderDone();
	
	/**
	 * Marks the provider as done.
	 */
	public void setProviderDone();
	
	/**
	 * Marks the provider as "not done".
	 */
	public void setProviderWorking();
	
	/**
	 * @return
	 * if the provider contains no items.
	 */
	public boolean isEmpty();
	
	/**
	 * Waits for the shutdown of this provider.
	 */
	public void waitForShutdown();
	
//	/**
//	 * Adds an object of type {@code V} to the provider that is in some
//	 * way connected to the provider.
//	 * @param element
//	 * is an element of type {@code V}
//	 */
//	public void addConnectedElement(V element);
	
}
