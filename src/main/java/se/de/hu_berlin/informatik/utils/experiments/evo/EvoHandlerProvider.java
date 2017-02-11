package se.de.hu_berlin.informatik.utils.experiments.evo;

import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInputAndReturn;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInputAndReturnMethodProvider;

public abstract class EvoHandlerProvider<T,F extends Comparable<F>> extends EHWithInputAndReturnMethodProvider<EvoItem<T,F>,EvoItem<T, F>> {

	@Override
	public EvoItem<T, F> processInput(EvoItem<T,F> input, EHWithInputAndReturn<EvoItem<T,F>, EvoItem<T, F>> executingHandler) {
		input.setFitness(computeFitness(input.getItem()));
		return input;
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
