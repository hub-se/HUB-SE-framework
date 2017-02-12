package se.de.hu_berlin.informatik.utils.experiments.evo;

public interface EvoRecombination<T> {

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
	
	public int getIDofNextRecombination(T parent1, T parent2);
	
}
