package se.de.hu_berlin.informatik.utils.experiments.evo;

import java.util.Collection;

public interface EvoMutationProvider<T,L,K extends Comparable<K>> {

	public static enum MutationSelectionStrategy {
		RANDOM
	}
	
	/**
	 * Produces a mutation that can be applied to an object of type T, given a location of type L.
	 * Uses the given strategy to pick a mutation.
	 * @param strategy
	 * the strategy to use when choosing a mutation
	 * @return
	 * the mutation
	 */
	public EvoMutation<T,L,K> getNextMutationTemplate(MutationSelectionStrategy strategy);
	
	/**
	 * Produces a mutation that can be applied to an object of type T, given a location of type L.
	 * Uses the provider's own strategy to pick a mutation.
	 * @return
	 * the mutation
	 */
	default public EvoMutation<T,L,K> getNextMutationTemplate() {
		return getNextMutationTemplate(getMutationSelectionStrategy());
	}
	
	/**
	 * Adds the given mutation to the collection of possible mutations.
	 * @param mutation
	 * the mutation to add
	 * @return
	 * true if successful; false otherwise
	 */
	public boolean addMutationTemplate(EvoMutation<T,L,K> mutation);
	
	/**
	 * Adds the given mutations to the collection of possible mutations.
	 * @param mutations
	 * the mutations to add
	 * @return
	 * true if successful; false otherwise
	 */
	default public boolean addMutationTemplates(Collection<EvoMutation<T,L,K>> mutations) {
		boolean result = true;
		for (EvoMutation<T,L,K> mutation : mutations) {
			result &= addMutationTemplate(mutation);
		}
		return result;
	}
	
	/**
	 * Adds the given mutations to the collection of possible mutations.
	 * @param mutations
	 * the mutations to add
	 * @return
	 * true if successful; false otherwise
	 */
	default public boolean addMutationTemplates(EvoMutation<T,L,K>... mutations) {
		boolean result = true;
		for (EvoMutation<T,L,K> mutation : mutations) {
			result &= addMutationTemplate(mutation);
		}
		return result;
	}
	
	/**
	 * @return
	 * the collection of mutations in the pool
	 */
	public Collection<EvoMutation<T,L,K>> getMutations();
	
	public EvoMutationProvider<T,L,K> setMutationSelectionStrategy(MutationSelectionStrategy mutationSelectionStrategy);
	
	public MutationSelectionStrategy getMutationSelectionStrategy();
}
