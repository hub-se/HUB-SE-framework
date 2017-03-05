package se.de.hu_berlin.informatik.utils.experiments.evo;

import se.de.hu_berlin.informatik.utils.tm.AbstractTransmitter;

public abstract class EvoFitnessChecker<T,F extends Comparable<F>> extends AbstractTransmitter<EvoItem<T,F>,EvoItem<T, F>> {

	@Override
	public EvoItem<T, F> processItem(EvoItem<T, F> item) {
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
