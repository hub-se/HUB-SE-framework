package se.de.hu_berlin.informatik.utils.experiments.evo;

public interface EvoLocationProvider<T,L> {

	public static enum LocationSelectionStrategy {
		RANDOM
	}
	
	/**
	 * Should produce a mutation location based on the given item
	 * and the given strategy.
	 * @param item
	 * the item to produce a location for
	 * @param strategy
	 * the strategy to use to produce the location
	 * @return
	 * the produced location
	 */
	public L getNextLocation(T item, LocationSelectionStrategy strategy);
	
	/**
	 * Should produce a mutation location based on the given item
	 * and the provider's own strategy.
	 * @param item
	 * the item to produce a location for
	 * @return
	 * the produced location
	 */
	default public L getNextLocation(T item) {
		return getNextLocation(item, getLocationSelectionStrategy());
	}
	
	public EvoLocationProvider<T,L> setLocationSelectionStrategy(LocationSelectionStrategy locationSelectionStrategy);
	
	public LocationSelectionStrategy getLocationSelectionStrategy();
	
}
