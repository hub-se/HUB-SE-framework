package se.de.hu_berlin.informatik.utils.experiments.evo;

import se.de.hu_berlin.informatik.utils.experiments.evo.EvoMutation.History;

public interface EvoItem<T,F extends Comparable<F>> extends Comparable<F> {

	/**
	 * @return
	 * the fitness of the item
	 */
	public F getFitness();
	
	/**
	 * @param fitness
	 * to set
	 */
	public void setFitness(F fitness);
	
	/**
	 * @return
	 * the item
	 */
	public T getItem();
	
	/**
	 * @param item
	 * to set
	 */
	public void setItem(T item);
	
	/**
	 * Cleans up any traces of this item in case it is
	 * not part of the population any more. This may clean up
	 * directory structures or nullify object pointers, etc.
	 * @return
	 * true if successful; false otherwise
	 */
	public boolean cleanUp();
	
	public History getMutationIdHistory();
	
	public void setMutationIdHistory(History history);
	
	public void addToMutationIdHistory(int id);
	
}
