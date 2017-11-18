package se.de.hu_berlin.informatik.utils.experiments.evo;

public class SimpleEvoItemWithoutHistory<T,F extends Comparable<F>, K extends Comparable<K>> implements EvoItem<T,F,K> {

	private T item = null;
	private F fitness = null;
	
	public SimpleEvoItemWithoutHistory(T item) {
		this.item = item;
	}
	
	public SimpleEvoItemWithoutHistory(T item, F fitness) {
		this(item);
		this.fitness = fitness;
	}

	@Override
	public F getFitness() {
		return fitness;
	}
	
	@Override
	public void setFitness(F fitness) {
		this.fitness = fitness;
	}

	@Override
	public T getItem() {
		return item;
	}

	@Override
	public boolean cleanUp() {
		item = null;
		return true;
	}

	@Override
	public History<T,K> getHistory() {
		return null;
	}

	@Override
	public void setItem(T item) {
		this.item = item;
	}

	@Override
	public String toString() {
		return this.fitness == null ? "null" : this.fitness.toString();
	}

}
