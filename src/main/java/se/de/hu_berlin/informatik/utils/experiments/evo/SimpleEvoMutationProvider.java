package se.de.hu_berlin.informatik.utils.experiments.evo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class SimpleEvoMutationProvider<T,L> implements EvoMutationProvider<T,L> {

	private MutationSelectionStrategy strategy = MutationSelectionStrategy.RANDOM;
	private final List<EvoMutation<T,L>> availableMutations = new ArrayList<>();
	private Random random = new Random(); 

	@Override
	public EvoMutation<T, L> getNextMutationTemplate(MutationSelectionStrategy strategy) {
		if (availableMutations.isEmpty()) {
			throw new IllegalStateException("No mutations were added to the provider.");
		}
		if (availableMutations.size() == 1) {
			return availableMutations.get(0);
		}
		switch (strategy) {
		case RANDOM:
			return availableMutations.get(random.nextInt(availableMutations.size()));
		default:
			throw new UnsupportedOperationException("Not implemented yet.");
		}
	}

	@Override
	public boolean addMutationTemplate(EvoMutation<T, L> mutation) {
		return availableMutations.add(mutation);
	}

	@Override
	public Collection<EvoMutation<T, L>> getMutations() {
		return availableMutations;
	}

	@Override
	public EvoMutationProvider<T,L> setMutationSelectionStrategy(MutationSelectionStrategy mutationSelectionStrategy) {
		this.strategy = mutationSelectionStrategy;
		return this;
	}

	@Override
	public MutationSelectionStrategy getMutationSelectionStrategy() {
		return this.strategy;
	}

}
