package se.de.hu_berlin.informatik.utils.experiments.evo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class SimpleEvoRecombinationProvider<T,K extends Comparable<K>> implements EvoRecombinationProvider<T,K> {

	private RecombinationTypeSelectionStrategy strategy = RecombinationTypeSelectionStrategy.RANDOM;
	private final List<EvoRecombination<T,K>> availableRecombinations = new ArrayList<>();
	private Random random = new Random(); 
	
	@Override
	public EvoRecombination<T,K> getNextRecombinationType(RecombinationTypeSelectionStrategy strategy) throws IllegalStateException {
		if (availableRecombinations.isEmpty()) {
			throw new IllegalStateException("No recombinations were added to the provider.");
		}
		if (availableRecombinations.size() == 1) {
			return availableRecombinations.get(0);
		}
		switch (strategy) {
		case RANDOM:
			return availableRecombinations.get(random.nextInt(availableRecombinations.size()));
		default:
			throw new UnsupportedOperationException("Not implemented yet.");
		}
	}

	@Override
	public boolean addRecombinationTemplate(EvoRecombination<T,K> recombination) {
		return availableRecombinations.add(recombination);
	}

	@Override
	public Collection<EvoRecombination<T,K>> getRecombinations() {
		return availableRecombinations;
	}

	@Override
	public EvoRecombinationProvider<T,K> setRecombinationTypeSelectionStrategy(RecombinationTypeSelectionStrategy recombinationTypeSelectionStrategy) {
		this.strategy = recombinationTypeSelectionStrategy;
		return this;
	}

	@Override
	public RecombinationTypeSelectionStrategy getRecombinationTypeSelectionStrategy() {
		return this.strategy;
	}

}
