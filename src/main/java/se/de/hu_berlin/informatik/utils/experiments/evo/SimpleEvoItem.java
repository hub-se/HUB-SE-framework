package se.de.hu_berlin.informatik.utils.experiments.evo;

import se.de.hu_berlin.informatik.utils.experiments.evo.EvoMutation.History;

public class SimpleEvoItem<T,F extends Comparable<F>> implements EvoItem<T,F> {

	private T item = null;
	private F fitness = null;
	private History history;
	
	public SimpleEvoItem(T item) {
		this.item = item;
		this.history = new History();
	}
	
	public SimpleEvoItem(T item, F fitness) {
		this(item);
		this.fitness = fitness;
	}
	
	public SimpleEvoItem(T item, History history) {
		this.item = item;
		this.history = new History(history);
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
	public History getMutationIdHistory() {
		return history;
	}

	@Override
	public void addToMutationIdHistory(int id) {
		history.add(id);
	}

	@Override
	public void setItem(T item) {
		this.item = item;
	}

	@Override
	public void setMutationIdHistory(History history) {
		this.history = new History(history);
	}

}
