package se.de.hu_berlin.informatik.utils.experiments.evo;

import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInputAndReturn;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInputAndReturnMethodProvider;

public abstract class EvoHandlerProvider<T,F> extends EHWithInputAndReturnMethodProvider<T,EvoResult<T, F>> {

	@Override
	public EvoResult<T, F> processInput(T input, EHWithInputAndReturn<T, EvoResult<T, F>> executingHandler) {
		return computeFitness(input);
	}

	/**
	 * Computes the fitness of the given item.
	 * @param item
	 * the item to compute the fitness from
	 * @return
	 * a result item
	 */
	public abstract EvoResult<T, F> computeFitness(T item);

}
