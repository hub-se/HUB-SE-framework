package se.de.hu_berlin.informatik.utils.experiments.evo;

public abstract class AbstractEvoLocationProvider<T,L> implements EvoLocationProvider<T,L> {

	private LocationSelectionStrategy strategy = LocationSelectionStrategy.RANDOM;

	@Override
	public EvoLocationProvider<T,L> setLocationSelectionStrategy(LocationSelectionStrategy locationSelectionStrategy) {
		this.strategy = locationSelectionStrategy;
		return this;
	}

	@Override
	public LocationSelectionStrategy getLocationSelectionStrategy() {
		return this.strategy;
	}
	
}
