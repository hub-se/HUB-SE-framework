package se.de.hu_berlin.informatik.utils.experiments.evo;

public class SimpleEvoItem<T,F extends Comparable<F>, K extends Comparable<K>> implements EvoItem<T,F,K> {

	private T item = null;
	private F fitness = null;
	private History<T,K> history;
	
	public SimpleEvoItem(T item) {
		this.item = item;
		this.history = new History<>(item);
	}
	
	public SimpleEvoItem(T item, F fitness) {
		this(item);
		this.fitness = fitness;
	}
	
	public SimpleEvoItem(T item, History<T,K> history, EvoID<K> mutationId) {
		this.item = item;
		this.history = new History<>(history, mutationId);
	}
	
	public SimpleEvoItem(T item, History<T,K> parentHistory1, History<T,K> parentHistory2, EvoID<K> recombinationId) {
		this.item = item;
		this.history = new History<>(parentHistory1, parentHistory2, recombinationId);
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
		return history;
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
