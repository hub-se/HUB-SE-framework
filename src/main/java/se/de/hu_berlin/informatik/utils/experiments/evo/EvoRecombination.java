package se.de.hu_berlin.informatik.utils.experiments.evo;

public interface EvoRecombination<T, K extends Comparable<K>> {

	/**
	 * Recombines the given parent objects and produces a child object. (cross-over)
	 * @param parent1
	 * the first parent
	 * @param parent2
	 * the second parent
	 * @return
	 * the child object
	 */
	public T recombine(T parent1, T parent2);
	
	/**
	 * Returns an id for the next recombination that will be applied by calling 
	 * {@link #recombine(Object, Object)} with the given parent objects.
	 * Any random decisions within the recombination procedure should be
	 * made by this point to be able to compute a unique id that reflects
	 * these random decisions. This id is used to track the history of the item.
	 * @param parent1
	 * the first parent
	 * @param parent2
	 * the second parent
	 * @return
	 * the id
	 */
	public EvoID<K> getIDofNextRecombination(T parent1, T parent2);
	
}
