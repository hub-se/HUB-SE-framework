package se.de.hu_berlin.informatik.utils.experiments.evo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.RecombinationTypeSelectionStrategy;

public class SimpleEvoRecombinationProvider<T> implements EvoRecombinationProvider<T> {

	private final List<EvoRecombination<T>> availableRecombinations = new ArrayList<>();
	private Random random = new Random(); 
	
	@Override
	public EvoRecombination<T> getNextRecombinationType(RecombinationTypeSelectionStrategy strategy) throws IllegalStateException {
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
	public boolean addRecombinationTemplate(EvoRecombination<T> recombination) {
		return availableRecombinations.add(recombination);
	}

	@Override
	public Collection<EvoRecombination<T>> getRecombinations() {
		return availableRecombinations;
	}

}
