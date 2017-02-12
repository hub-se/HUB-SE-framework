package se.de.hu_berlin.informatik.utils.experiments.evo;

public class SimpleEvoItem<T,F extends Comparable<F>> implements EvoItem<T,F> {

	private T item = null;
	private F fitness = null;
	private History<T> history;
	
	public SimpleEvoItem(T item) {
		this.item = item;
		this.history = new History<>(item);
	}
	
	public SimpleEvoItem(T item, F fitness) {
		this(item);
		this.fitness = fitness;
	}
	
	public SimpleEvoItem(T item, History<T> history, int mutationId) {
		this.item = item;
		this.history = new History<>(history, mutationId);
	}
	
	public SimpleEvoItem(T item, History<T> parentHistory1, History<T> parentHistory2, int recombinationId) {
		this.item = item;
		this.history = new History<>(parentHistory1, parentHistory2, recombinationId);
	}
	
	@Override
	public int compareTo(F o) {
		return o.compareTo(this.getFitness());
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
	public History<T> getHistory() {
		return history;
	}

	@Override
	public void setItem(T item) {
		this.item = item;
	}

}
