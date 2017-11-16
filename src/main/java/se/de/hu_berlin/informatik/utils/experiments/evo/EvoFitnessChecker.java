package se.de.hu_berlin.informatik.utils.experiments.evo;

import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;

public abstract class EvoFitnessChecker<T,F extends Comparable<F>, K extends Comparable<K>> extends AbstractProcessor<EvoItem<T,F,K>,EvoItem<T,F,K>> {

	@Override
	public EvoItem<T, F, K> processItem(EvoItem<T, F, K> item) {
		item.setFitness(computeFitness(item.getItem()));
		return item;
	}

	/**
	 * Computes the fitness of the given item.
	 * @param item
	 * the item to compute the fitness from
	 * @return
	 * a result item
	 */
	public abstract F computeFitness(T item);

}
