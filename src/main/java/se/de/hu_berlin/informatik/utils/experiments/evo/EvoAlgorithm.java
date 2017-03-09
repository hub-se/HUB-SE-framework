package se.de.hu_berlin.informatik.utils.experiments.evo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

import se.de.hu_berlin.informatik.utils.experiments.evo.EvoItem.History;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.processors.basics.CollectionSequencer;
import se.de.hu_berlin.informatik.utils.processors.basics.ThreadedProcessor;
import se.de.hu_berlin.informatik.utils.processors.sockets.pipe.PipeLinker;
import se.de.hu_berlin.informatik.utils.statistics.Statistics;
import se.de.hu_berlin.informatik.utils.statistics.StatisticsCollector;
import se.de.hu_berlin.informatik.utils.tracking.ProgressTracker;
import se.de.hu_berlin.informatik.utils.tracking.TrackingStrategy;

public class EvoAlgorithm<T,L,F extends Comparable<F>> {
	
	public static enum KillStrategy {
		KILL_25_PERCENT,
		KILL_50_PERCENT,
		KILL_75_PERCENT
	}
	
	public static enum PopulationSelectionStrategy {
		BEST_ONLY,
		HALF_BEST_HALF_RANDOM,
		RANDOM,
		REUSE_ALL
	}
	
	public static enum ParentSelectionStrategy {
		BEST_10_PERCENT,
		BEST_20_PERCENT,
		BEST_50_PERCENT,
		BEST_75_PERCENT,
		BEST_5_PERCENT_RANDOM_5_PERCENT,
		BEST_10_PERCENT_RANDOM_10_PERCENT,
		BEST_25_PERCENT_RANDOM_25_PERCENT,
		BEST_25_PERCENT_RANDOM_10_PERCENT,
		RANDOM_10_PERCENT,
		RANDOM_20_PERCENT,
		RANDOM_50_PERCENT,
		RANDOM_75_PERCENT,
		ALL
	}
	
	public static enum RecombinationStrategy {
		MONOGAMY_BEST_TO_WORST,
		MONOGAMY_RANDOM,
		POLYGAMY_SINGLE_BEST_WITH_OTHERS,
		POLYGAMY_BEST_5_PERCENT_WITH_OTHERS,
		POLYGAMY_BEST_10_PERCENT_WITH_OTHERS,
		POLYGAMY_BEST_20_PERCENT_WITH_OTHERS,
		POLYGAMY_BEST_50_PERCENT_WITH_OTHERS,
	}
	
	
	private final int populationCount;
	private final int maxGenerationBound;
	private final F fitnessGoal;
	
	private final KillStrategy killStrategy;
	private final PopulationSelectionStrategy populationSelectionStrategy;
	private final EvoRecombinationProvider<T> recombinationProvider;
	private final ParentSelectionStrategy parentSelectionStrategy;
	private final RecombinationStrategy recombinationStrategy;
	private final EvoLocationProvider<T,L> locationProvider;
	private final EvoMutationProvider<T,L> mutationProvider;
	private final PipeLinker evaluationPipe;
	
	private final List<T> initialPopulation;
	private final Set<History<T>> appliedMutations = new HashSet<>();
	
	private final StatisticsCollector<EvoStatistics> collector;
	private final boolean collectorAvailable;
	
	private final TrackingStrategy tracker = new ProgressTracker(false);
	
	private EvoAlgorithm(Builder<T, L, F> builder) {
		this.populationCount = builder.populationCount;
		if (this.populationCount < 1) {
			throw new IllegalStateException("Population count has to be positive.");
		}
		this.maxGenerationBound = builder.maxGenerationBound;
		if (this.maxGenerationBound < 1) {
			throw new IllegalStateException("Generation bound has to be positive.");
		}
		this.fitnessGoal = builder.fitnessGoal;
		if (this.fitnessGoal == null) {
			throw new IllegalStateException("Fitness goal not given.");
		}
		this.killStrategy = builder.killStrategy;
		if (this.killStrategy == null) {
			throw new IllegalStateException("Kill strategy not given.");
		}
		this.populationSelectionStrategy = builder.populationSelectionStrategy;
		if (this.populationSelectionStrategy == null) {
			throw new IllegalStateException("Population selection strategy not given.");
		}
		this.parentSelectionStrategy = builder.parentSelectionStrategy;
		if (this.parentSelectionStrategy == null) {
			throw new IllegalStateException("Parent selection strategy not given.");
		}
		
		this.recombinationProvider = builder.recombinationProvider;
		this.recombinationStrategy = builder.recombinationStrategy;
		if (this.recombinationProvider != null) {
			if (this.recombinationStrategy == null) {
				throw new IllegalStateException("Recombination strategy not given.");
			}
		}
		
		this.locationProvider = builder.locationProvider;
		if (this.locationProvider == null) {
			throw new IllegalStateException("Location provider not given.");
		}
		
		this.mutationProvider = builder.mutationProvider;
		if (this.mutationProvider == null) {
			throw new IllegalStateException("Mutation provider not given (or no mutation templates added).");
		}
		
		this.evaluationPipe = builder.evaluationPipe;
		if (this.evaluationPipe == null) {
			throw new IllegalStateException("Fitness checker not given.");
		}
		
		this.initialPopulation = builder.initialPopulation;
		if (this.initialPopulation == null || this.initialPopulation.isEmpty()) {
			throw new IllegalStateException("No initial population given.");
		}
		
		this.collector = builder.collector;
		if (this.collector == null) {
			collectorAvailable = false;
		} else {
			collectorAvailable = true;
		}
	}
	
	public EvoItem<T,F> start() {
		if (collectorAvailable) {
			Statistics<EvoStatistics> staticStatistics = new Statistics<>();
			staticStatistics.addStatisticsElement(EvoStatistics.MAX_GENERATION_COUNT, maxGenerationBound);
			staticStatistics.addStatisticsElement(EvoStatistics.POPULATION_SIZE, populationCount);
			
			collector.addStatistics(staticStatistics);
		}
		
		//provide initial item/initial population
		if (initialPopulation.isEmpty()) {
			return null;
		}
		
		//initialize current population list
		List<EvoItem<T,F>> currentPopulation = new ArrayList<>(populationCount);
		//populate with selected old population
		for (T item : initialPopulation) {
			currentPopulation.add(new SimpleEvoItem<>(item));
		}
		
		int generationCounter= 1;
		{
			Statistics<EvoStatistics> statistics = new Statistics<>();
			statistics.addStatisticsElement(EvoStatistics.GENERATION_COUNT, 1);
			tracker.track("...running starting generation");
			//fill up with mutants if below desired population size
			int initialChildrenCount = populationCount - currentPopulation.size();
			currentPopulation.addAll(produceMutationBasedOffspring(currentPopulation, 
					initialChildrenCount, statistics));

			//test/validate (evaluation)
			currentPopulation = calculateFitness(currentPopulation, statistics);
			
			//collect some statistics
			if (collectorAvailable) {
				collector.addStatistics(statistics);
			}
		}
		
		//loop while the generation bound isn't reached and the fitness goal isn't met by any item
		while (generationCounter < maxGenerationBound && !checkIfGoalIsMet(currentPopulation)) {
			Statistics<EvoStatistics> statistics = new Statistics<>();
			statistics.addStatisticsElement(EvoStatistics.GENERATION_COUNT, 1);
			++generationCounter;
			tracker.track("...running generation " + generationCounter);
			
			//choose items for new population (selection)
			currentPopulation = selectNewPopulationAndKillRemaining(currentPopulation);
			
			//select population for reproduction (parents)
			List<EvoItem<T, F>> parents = selectParents(currentPopulation);
			
			//produce new offspring through recombination, if possible
			if (recombinationProvider != null) {
				//cross-over (recombination)
				int childrenCount = populationCount - currentPopulation.size();
				currentPopulation.addAll(produceRecombinationalOffspring(parents, childrenCount, statistics));
			} 

			//fill up with mutants if below desired population size
			int childrenCount = populationCount - currentPopulation.size();
			currentPopulation.addAll(produceMutationBasedOffspring(parents, childrenCount, statistics));
			
			//mutate the current population
			currentPopulation = mutatePopulation(currentPopulation, statistics);
			
			//test and validate (evaluation)
			currentPopulation = calculateFitness(currentPopulation, statistics);
			
			//collect some statistics
			if (collectorAvailable) {
				collector.addStatistics(statistics);
			}
		} //loop end
		
		//return best item, discard the rest
		return selectBestItem(currentPopulation, true);
	}

	private boolean checkIfGoalIsMet(List<EvoItem<T, F>> currentEvaluatedPop) {
		for (EvoItem<T,F> evaluatedItem : currentEvaluatedPop) {
			if (evaluatedItem.compareTo(fitnessGoal) >= 0) {
				return true;
			}
		}
		return false;
	}

	private List<EvoItem<T,F>> produceMutationBasedOffspring(List<EvoItem<T,F>> parents, int childrenCount, 
			Statistics<EvoStatistics> statistics) {
		childrenCount = childrenCount < 0 ? 0 : childrenCount;
		if (childrenCount == 0) {
			return Collections.emptyList();
		} else {
			Log.out(EvoAlgorithm.class, "Filling up with mutation based offspring from %d parents. (goal: %d)", parents.size(), childrenCount);
			List<EvoItem<T,F>> children = new ArrayList<>(childrenCount);

			Collections.shuffle(parents);
			int i = 0;
			int duplicateCount = 0;
			while (i < childrenCount) {
				//iterate through original population and generate mutants 
				//until the population is filled to the desired count
				Iterator<EvoItem<T,F>> iterator = parents.iterator();
				while (iterator.hasNext()) {
					EvoItem<T,F> item = iterator.next();
					//TODO: what if no new mutation can be found? infinite loop...
					boolean mutationApplied = false;
					int tryCount = 0;
					while(!mutationApplied && tryCount < 1) {
						++tryCount;
						L nextLocation = locationProvider.getNextLocation(item.getItem());
						EvoMutation<T,L> mutation = mutationProvider.getNextMutationTemplate();
						EvoID mutationId = mutation.getIDofNextMutation(item.getItem(), nextLocation);

						if (!mutationWasAlreadyApplied(item.getHistory(), mutationId)) {
							//apply the mutation and replace the item
							EvoItem<T, F> mutant = new SimpleEvoItem<T, F>(mutation.applyTo(item.getItem(), nextLocation), 
									item.getHistory(), mutationId);
							//add the mutation history to the set of already applied mutation sequences
							appliedMutations.add(mutant.getHistory().copy());
							mutationApplied = true;
							children.add(mutant);
						} else {
							++duplicateCount;
						}
					}
					++i;
				}
			}
			statistics.addStatisticsElement(EvoStatistics.DUPLICATE_ELEMENTS_COUNT, duplicateCount);
			
			return children;
		}
	}

	private static <T, F extends Comparable<F>> EvoItem<T,F> selectBestItem(List<EvoItem<T, F>> currentEvaluatedPop, boolean cleanUpRest) {
		if (currentEvaluatedPop.isEmpty()) {
			return null;
		}
		EvoItem<T,F> bestItem = currentEvaluatedPop.iterator().next();
		for (EvoItem<T,F> evaluatedItem : currentEvaluatedPop) {
			if (evaluatedItem.compareTo(bestItem.getFitness()) > 0) {
				bestItem = evaluatedItem;
			}
		}
		if (cleanUpRest) {
			cleanUpOtherItems(currentEvaluatedPop, bestItem);
		}
		return bestItem;
	}

	private List<EvoItem<T,F>> mutatePopulation(List<EvoItem<T,F>> currentPopulation, 
			Statistics<EvoStatistics> statistics) {
		Log.out(EvoAlgorithm.class, "Mutating %d elements.", currentPopulation.size());
		int duplicateCount = 0;
		for (EvoItem<T,F> item : currentPopulation) {
			//TODO: what if no new mutation can be found? infinite loop...
			boolean mutationApplied = false;
			int tryCount = 0;
			
			while(!mutationApplied && tryCount < 50) {
				++tryCount;
				L nextLocation = locationProvider.getNextLocation(item.getItem());
				EvoMutation<T,L> mutation = mutationProvider.getNextMutationTemplate();
				EvoID mutationId = mutation.getIDofNextMutation(item.getItem(), nextLocation);

				if (!mutationWasAlreadyApplied(item.getHistory(), mutationId)) {
					//apply the mutation and replace the item
					item.setItem(mutation.applyTo(item.getItem(), nextLocation));
					//add the mutation id to the history of the item
					item.addMutationIdToHistory(mutationId);
					//add the mutation history to the set of already applied mutation sequences
					appliedMutations.add(item.getHistory().copy());
					mutationApplied = true;
				} else {
					++duplicateCount;
				}
			}
		}
		statistics.addStatisticsElement(EvoStatistics.DUPLICATE_ELEMENTS_COUNT, duplicateCount);
		
		return currentPopulation;
	}

	private boolean mutationWasAlreadyApplied(History<T> history, EvoID mutationId) {
		History<T> temp = new History<>(history);
		temp.addMutationId(mutationId);
		if (appliedMutations.contains(temp)) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean recombinationWasAlreadyApplied(History<T> history1, History<T> history2, EvoID recombinationId) {
		History<T> temp = new History<>(history1, history2, recombinationId);
		if (appliedMutations.contains(temp)) {
			return true;
		} else {
			return false;
		}
	}

	private List<EvoItem<T,F>> produceRecombinationalOffspring(List<EvoItem<T, F>> parents, int childrenCount, 
			Statistics<EvoStatistics> statistics) {
		if (parents.size() < 2) {
			Log.warn(EvoAlgorithm.class, "Need at least 2 parents to produce offspring!");
			return Collections.emptyList();
		} else {
			Log.out(EvoAlgorithm.class, "Producing recombination based offspring from %d parents. (goal: %d)", 
					parents.size(), childrenCount);
			List<EvoItem<T,F>> children = new ArrayList<>();

			switch (recombinationStrategy) {
			case MONOGAMY_BEST_TO_WORST:
				sortBestToWorst(parents);
				monogamyChildrenProduction(parents, childrenCount, children, statistics);
				break;
			case MONOGAMY_RANDOM:
				Collections.shuffle(parents);
				monogamyChildrenProduction(parents, childrenCount, children, statistics);
				break;
			case POLYGAMY_BEST_5_PERCENT_WITH_OTHERS:
				polygamyChildrenProduction(parents, childrenCount, children, parents.size() * 0.05, statistics);
				break;
			case POLYGAMY_BEST_10_PERCENT_WITH_OTHERS:
				polygamyChildrenProduction(parents, childrenCount, children, parents.size() * 0.1, statistics);
				break;
			case POLYGAMY_BEST_20_PERCENT_WITH_OTHERS:
				polygamyChildrenProduction(parents, childrenCount, children, parents.size() * 0.2, statistics);
				break;
			case POLYGAMY_BEST_50_PERCENT_WITH_OTHERS:
				polygamyChildrenProduction(parents, childrenCount, children, parents.size() * 0.5, statistics);
				break;
			case POLYGAMY_SINGLE_BEST_WITH_OTHERS:
				polygamyChildrenProduction(parents, childrenCount, children, 1, statistics);
				break;
			default:
				throw new UnsupportedOperationException("Not implemented, yet.");
			}

			//maybe, we have to remove some children now...
			removeRandomlyToSizeAndCleanUp(children, childrenCount);

			return children;
		}
	}

	private void polygamyChildrenProduction(List<EvoItem<T, F>> parentPopulation, int childrenCount, 
			List<EvoItem<T,F>> children, double number, Statistics<EvoStatistics> statistics) {
		List<EvoItem<T, F>> bestParents = new ArrayList<>();
		List<EvoItem<T, F>> population = new ArrayList<>(parentPopulation);
		sortBestToWorst(population);
		transferNumberOfFirstItemsToCollector(population, number, 1, bestParents);
		Log.out(EvoAlgorithm.class, "Polygamy... strong parent count: %d, weak parent count: %d", 
				bestParents.size(), population.size());
		int i = 0;
		EvoItem<T, F> parent1 = null;
		EvoItem<T, F> parent2 = null;
		int duplicateCount = 0;
		//repeat until all children are generated
		while (i < childrenCount) {
			//start iterating over the best parents
			Iterator<EvoItem<T,F>> iterator = bestParents.iterator();
			while (iterator.hasNext()) {
				//get parent1
				parent1 = iterator.next();
				
				//start iterating over the other parents
				Iterator<EvoItem<T,F>> iterator2 = population.iterator();
				while (iterator2.hasNext()) {
					//get parent2
					parent2 = iterator2.next();

					//TODO: what if no new recombination can be found? infinite loop...
					boolean recombinationApplied = false;
					int tryCount = 0;
					while(!recombinationApplied && tryCount < 50) {
						++tryCount;
						//if both parents are picked, produce a child
						EvoRecombination<T> recombination = recombinationProvider.getNextRecombinationType();
						EvoID recombinationId = recombination.getIDofNextRecombination(parent1.getItem(), parent2.getItem());

						if (!recombinationWasAlreadyApplied(parent1.getHistory(), parent2.getHistory(), recombinationId)) {
							//apply the recombination and produce the child
							EvoItem<T,F> child = new SimpleEvoItem<>(
									recombination.recombine(parent1.getItem(), parent2.getItem()), 
									parent1.getHistory(), parent2.getHistory(), recombinationId);
							//add the child history to the set of already seen histories
							appliedMutations.add(child.getHistory().copy());
							recombinationApplied = true;
							children.add(child);
						} else {
							++duplicateCount;
						}
					}
					++i;
				}
			}
		}
		statistics.addStatisticsElement(EvoStatistics.DUPLICATE_ELEMENTS_COUNT, duplicateCount);
	}

	private void monogamyChildrenProduction(List<EvoItem<T, F>> parentPopulation, int childrenCount, 
			List<EvoItem<T,F>> children, Statistics<EvoStatistics> statistics) {
		Log.out(EvoAlgorithm.class, "Monogamy... parent count: %d", parentPopulation.size());
		int i = 0;
		EvoItem<T, F> parent1 = null;
		EvoItem<T, F> parent2 = null;
		//repeat until all children are generated
		while (i < childrenCount) {
			//start iterating over the parents
			Iterator<EvoItem<T,F>> iterator = parentPopulation.iterator();
			while (iterator.hasNext()) {
				//first get parent1, then parent2
				if (parent1 == null) {
					parent1 = iterator.next();
				} else if (parent2 == null) {
					parent2 = iterator.next();
				} 
				
				//if both parents are picked, produce a child and reset the parents
				if (parent1 != null && parent2 != null) {
					produceNewChild(parent1, parent2, children, statistics);
					++i;
					parent1 = null;
					parent2 = null;
				}
			}
		}
	}

	private void produceNewChild(EvoItem<T, F> parent1, EvoItem<T, F> parent2, List<EvoItem<T, F>> children, 
			Statistics<EvoStatistics> statistics) {
		//TODO: what if no new recombination can be found? infinite loop...
		boolean recombinationApplied = false;
		int tryCount = 0;
		int duplicateCount = 0;
		while(!recombinationApplied && tryCount < 50) {
			++tryCount;
			//if both parents are picked, produce a child
			EvoRecombination<T> recombination = recombinationProvider.getNextRecombinationType();
			EvoID recombinationId = recombination.getIDofNextRecombination(parent1.getItem(), parent2.getItem());

			if (!recombinationWasAlreadyApplied(parent1.getHistory(), parent2.getHistory(), recombinationId)) {
				//apply the recombination and produce the child
				EvoItem<T,F> child = new SimpleEvoItem<>(recombination.recombine(parent1.getItem(), parent2.getItem()), 
						parent1.getHistory(), parent2.getHistory(), recombinationId);
				//add the child history to the set of already seen histories
				appliedMutations.add(child.getHistory().copy());
				recombinationApplied = true;
				children.add(child);
			} else {
				++duplicateCount;
			}
		}
		statistics.addStatisticsElement(EvoStatistics.DUPLICATE_ELEMENTS_COUNT, duplicateCount);
	}
	
	private List<EvoItem<T, F>> selectParents(List<EvoItem<T, F>> evaluatedPop) {
		Log.out(EvoAlgorithm.class, "Selecting parents from %d elements.", evaluatedPop.size());
		if (evaluatedPop.size() < 2) {
			Log.warn(EvoAlgorithm.class, "Population size too small to select enough parents!");
			return Collections.emptyList();
		} else {
			double bestPercentage = 0;
			double randomPercentage = 0;
			double worstPercentage = 0;

			switch (parentSelectionStrategy) {
			case BEST_10_PERCENT:
				bestPercentage = evaluatedPop.size() * 0.1;
				break;
			case BEST_20_PERCENT:
				bestPercentage = evaluatedPop.size() * 0.2;
				break;
			case BEST_50_PERCENT:
				bestPercentage = evaluatedPop.size() * 0.5;
				break;
			case BEST_75_PERCENT:
				bestPercentage = evaluatedPop.size() * 0.75;
				break;
			case RANDOM_10_PERCENT:
				randomPercentage = evaluatedPop.size() * 0.1;
				break;
			case RANDOM_20_PERCENT:
				randomPercentage = evaluatedPop.size() * 0.2;
				break;
			case RANDOM_50_PERCENT:
				randomPercentage = evaluatedPop.size() * 0.5;
				break;
			case RANDOM_75_PERCENT:
				randomPercentage = evaluatedPop.size() * 0.75;
				break;
			case BEST_10_PERCENT_RANDOM_10_PERCENT:
				bestPercentage = evaluatedPop.size() * 0.1;
				randomPercentage = evaluatedPop.size() * 0.1;
				break;
			case BEST_25_PERCENT_RANDOM_25_PERCENT:
				bestPercentage = evaluatedPop.size() * 0.25;
				randomPercentage = evaluatedPop.size() * 0.25;
				break;
			case BEST_5_PERCENT_RANDOM_5_PERCENT:
				bestPercentage = evaluatedPop.size() * 0.05;
				randomPercentage = evaluatedPop.size() * 0.05;
				break;
			case BEST_25_PERCENT_RANDOM_10_PERCENT:
				bestPercentage = evaluatedPop.size() * 0.25;
				randomPercentage = evaluatedPop.size() * 0.10;
				break;
			case ALL:
				bestPercentage = evaluatedPop.size();
				break;
			default:
				throw new UnsupportedOperationException("Not implemented, yet.");
			}
			return selectForRecombination(evaluatedPop, bestPercentage, randomPercentage, worstPercentage);
		}
	}
	
	private static <T,F extends Comparable<F>> List<EvoItem<T, F>> selectForRecombination(List<EvoItem<T, F>> evaluatedPop,
			double bestNumber, double randomNumber, double worstNumber) {
		List<EvoItem<T, F>> parents = new ArrayList<>();
		List<EvoItem<T, F>> population = new ArrayList<>(evaluatedPop);
		
		int leastAmount = 1;
		if ((bestNumber == 0 && randomNumber == 0) || 
				(bestNumber == 0 && worstNumber == 0) || 
				(worstNumber == 0 && randomNumber == 0)) {
			leastAmount = 2;
		}

		if (bestNumber > 0) {
			sortBestToWorst(population);
			transferNumberOfFirstItemsToCollector(population, bestNumber, leastAmount, parents);
		}
		
		if (worstNumber > 0) {
			sortWorstToBest(population);
			transferNumberOfFirstItemsToCollector(population, worstNumber, leastAmount, parents);
		}
		
		if (randomNumber > 0) {
			//shuffle randomly
			Collections.shuffle(population);
			transferNumberOfFirstItemsToCollector(population, randomNumber, leastAmount, parents);
		}
		
		return parents;
	}

	private static <T,F extends Comparable<F>> void sortWorstToBest(List<EvoItem<T, F>> evaluatedPop) {
		//sort from smallest to biggest (worst to best)
		evaluatedPop.sort((o1,o2) -> o1.compareTo(o2.getFitness()));
	}

	private static <T,F extends Comparable<F>> void sortBestToWorst(List<EvoItem<T, F>> evaluatedPop) {
		//sort from biggest to smallest (best to worst)
		evaluatedPop.sort((o1,o2) -> o2.compareTo(o1.getFitness()));
	}

	private static <T,F extends Comparable<F>> void transferNumberOfFirstItemsToCollector(
			List<EvoItem<T, F>> sourcePopulation, double numberOfItems, int leastAmount, List<EvoItem<T, F>> collector) {
		//transfer at least 'leastAmount' items
		int transferCount = (int)numberOfItems > leastAmount ? (int)numberOfItems : leastAmount;
		int i = 0;
		Iterator<EvoItem<T,F>> iterator = sourcePopulation.iterator();
		while (iterator.hasNext() && i < transferCount) {
			++i;
			collector.add(iterator.next());
			iterator.remove();
		}
	}

	private List<EvoItem<T, F>> selectNewPopulationAndKillRemaining(List<EvoItem<T, F>> oldPopulation) {
		//kill off items to only keep part of the maximal population
		double selectionCount;
		switch (killStrategy) {
		case KILL_25_PERCENT:
			selectionCount = populationCount * 0.75;
			break;
		case KILL_50_PERCENT:
			selectionCount = populationCount * 0.50;
			break;
		case KILL_75_PERCENT:
			selectionCount = populationCount * 0.25;
			break;
		default:
			throw new UnsupportedOperationException("Not implemented, yet.");
		}
		Log.out(EvoAlgorithm.class, "Selecting %d from %d elements.", (int)selectionCount, oldPopulation.size());
		List<EvoItem<T, F>> resultPopulation = new ArrayList<>((int)selectionCount);

		switch (populationSelectionStrategy) {
		case BEST_ONLY:
			sortBestToWorst(oldPopulation);
			transferNumberOfFirstItemsToCollector(oldPopulation, selectionCount, 1, resultPopulation);
			break;
		case HALF_BEST_HALF_RANDOM:
			sortBestToWorst(oldPopulation);
			transferNumberOfFirstItemsToCollector(oldPopulation, selectionCount % 2 == 0 ? selectionCount/2 : selectionCount/2 + 1, 1, resultPopulation);
			Collections.shuffle(oldPopulation);
			transferNumberOfFirstItemsToCollector(oldPopulation, selectionCount/2, 1, resultPopulation);
			break;
		case RANDOM:
			Collections.shuffle(oldPopulation);
			transferNumberOfFirstItemsToCollector(oldPopulation, selectionCount, 1, resultPopulation);
			break;
		default:
			throw new UnsupportedOperationException("Not implemented, yet.");
		}
		
		//discard the remaining items
		cleanUpAllItems(oldPopulation);
		
		//maybe remove overpopulation that got through mistakenly
		removeRandomlyToSizeAndCleanUp(resultPopulation, (int)selectionCount);

		return resultPopulation;
	}
	
	private static <T,F extends Comparable<F>> void removeRandomlyToSizeAndCleanUp(
			List<EvoItem<T, F>> list, int maxNumberOfElements) {
		if (list.size() > maxNumberOfElements) {
			//remove randomly
			Collections.shuffle(list);
			int killCount = list.size() - maxNumberOfElements;
			Iterator<EvoItem<T, F>> iterator = list.iterator();
			int i = 0;
			while (iterator.hasNext() && i < killCount) {
				iterator.next();
				iterator.remove();
				++i;
			}
			//R.I.P.
		}
	}

	private static <T,F extends Comparable<F>> void cleanUpOtherItems(
			List<EvoItem<T, F>> evaluatedPop, EvoItem<T, F> evaluatedItem) {
		for (EvoItem<T,F> item : evaluatedPop) {
			if (evaluatedItem != item) {
				item.cleanUp();
			}
		}
	}
	
	private static <T,F extends Comparable<F>> void cleanUpAllItems(List<EvoItem<T, F>> list) {
		for (EvoItem<T,F> item : list) {
			item.cleanUp();
		}
	}

	private List<EvoItem<T, F>> calculateFitness(List<EvoItem<T,F>> population, Statistics<EvoStatistics> statistics) {
		Log.out(EvoAlgorithm.class, "Checking fitness for %d elements.", population.size());
		//check all elements in the population for their fitness values
		evaluationPipe.submitAndShutdown(population);
		
		statistics.addStatisticsElement(EvoStatistics.EVALUATED_ELEMENTS_COUNT, population.size()); 
		statistics.addStatisticsElement(EvoStatistics.BEST_FITNESS_REPRESENTATIONS, 
				selectBestItem(population, false).toString());
		//return a list with the evaluated population
		return population;
	}

	
	
	public static class Builder<T,L,F extends Comparable<F>> {
		
		private int populationCount;
		private int maxGenerationBound;
		private F fitnessGoal;
		
		private KillStrategy killStrategy = KillStrategy.KILL_25_PERCENT;
		private PopulationSelectionStrategy populationSelectionStrategy = PopulationSelectionStrategy.HALF_BEST_HALF_RANDOM;
		
		private EvoRecombinationProvider<T> recombinationProvider;
		private ParentSelectionStrategy parentSelectionStrategy = ParentSelectionStrategy.BEST_50_PERCENT;
		private RecombinationStrategy recombinationStrategy = RecombinationStrategy.POLYGAMY_BEST_20_PERCENT_WITH_OTHERS;
		
		private EvoMutationProvider<T,L> mutationProvider;
		
		private EvoLocationProvider<T,L> locationProvider;
		
		private PipeLinker evaluationPipe;
		
		private List<T> initialPopulation = new ArrayList<>();
		private StatisticsCollector<EvoStatistics> collector = null;
		
		public Builder(int populationCount, int maxGenerationBound) {
			super();
			this.populationCount = populationCount;
			this.maxGenerationBound = maxGenerationBound;
		}
		
		public Builder(int populationCount, int maxGenerationBound,
				KillStrategy killStrategy, PopulationSelectionStrategy populationSelectionStrategy, 
				ParentSelectionStrategy parentSelectionStrategy) {
			this(populationCount, maxGenerationBound);
			this.killStrategy = killStrategy;
			this.populationSelectionStrategy = populationSelectionStrategy;
			this.parentSelectionStrategy = parentSelectionStrategy;
		}
		
		public Builder(int populationCount, int maxGenerationBound,
				KillStrategy killStrategy, PopulationSelectionStrategy populationSelectionStrategy, 
				ParentSelectionStrategy parentSelectionStrategy,
				RecombinationStrategy recombinationStrategy) {
			this(populationCount, maxGenerationBound, killStrategy, populationSelectionStrategy, parentSelectionStrategy);
			this.recombinationStrategy = recombinationStrategy;
		}
		
		public Builder<T, L, F> setFitnessChecker(EvoFitnessChecker<T, F> evaluationHandlerProvider, int threadCount, F fitnessGoal) {
			this.fitnessGoal = fitnessGoal;

			this.evaluationPipe = new PipeLinker(); 
			this.evaluationPipe.append(
					new CollectionSequencer<EvoItem<T,F>>(),
					new ThreadedProcessor<>(threadCount, evaluationHandlerProvider)
					);
			
			return this;
		}
		
		public Builder<T, L, F> setLocationProvider(EvoLocationProvider<T,L> locationProvider) {
			this.locationProvider = locationProvider;
			return this;
		}
		
		public Builder<T, L, F> setAlternativeMutationProvider(EvoMutationProvider<T,L> mutationProvider) {
			this.mutationProvider = mutationProvider;
			return this;
		}
		
		public Builder<T, L, F> addMutationTemplate(EvoMutation<T,L> mutation) {
			initializeMutationProviderIfNull();
			this.mutationProvider.addMutationTemplate(mutation);
			return this;
		}
		
		public Builder<T, L, F> addMutationTemplates(@SuppressWarnings("unchecked") EvoMutation<T,L>... mutations) {
			initializeMutationProviderIfNull();
			for (EvoMutation<T,L> mutation : mutations) {
				this.mutationProvider.addMutationTemplate(mutation);
			}
			return this;
		}
		
		public Builder<T, L, F> addMutationTemplates(Collection<EvoMutation<T,L>> mutations) {
			initializeMutationProviderIfNull();
			for (EvoMutation<T,L> mutation : mutations) {
				this.mutationProvider.addMutationTemplate(mutation);
			}
			return this;
		}
		
		private void initializeMutationProviderIfNull() {
			if (this.mutationProvider == null) {
				this.mutationProvider = new SimpleEvoMutationProvider<>();
			}
		}
		
		public Builder<T, L, F> setAlternativeRecombinationProvider(EvoRecombinationProvider<T> recombinationProvider) {
			this.recombinationProvider = recombinationProvider;
			return this;
		}
		
		public Builder<T, L, F> addRecombinationTemplate(EvoRecombination<T> recombination) {
			initializeRecombinerIfNull();
			this.recombinationProvider.addRecombinationTemplate(recombination);
			return this;
		}
		
		public Builder<T, L, F> addRecombinationTemplates(@SuppressWarnings("unchecked") EvoRecombination<T>... recombinations) {
			initializeRecombinerIfNull();
			for (EvoRecombination<T> recombination : recombinations) {
				this.recombinationProvider.addRecombinationTemplate(recombination);
			}
			return this;
		}
		
		public Builder<T, L, F> addRecombinationTemplates(Collection<EvoRecombination<T>> recombinations) {
			initializeRecombinerIfNull();
			for (EvoRecombination<T> recombination : recombinations) {
				this.recombinationProvider.addRecombinationTemplate(recombination);
			}
			return this;
		}
		
		private void initializeRecombinerIfNull() {
			if (this.recombinationProvider == null) {
				this.recombinationProvider = new SimpleEvoRecombinationProvider<>();
			}
		}
		
		public EvoAlgorithm<T, L, F> build() {
			return new EvoAlgorithm<T,L,F>(this);
		}
		
		public Builder<T, L, F> addToInitialPopulation(T item) {
			initialPopulation.add(item);
			return this;
		}

		public Builder<T, L, F> setStatisticsCollector(StatisticsCollector<EvoStatistics> collector) {
			this.collector  = collector;
			return this;
		}
		
	}

}
