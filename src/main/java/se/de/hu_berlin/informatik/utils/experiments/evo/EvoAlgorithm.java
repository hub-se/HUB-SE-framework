package se.de.hu_berlin.informatik.utils.experiments.evo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.PipeLinker;
import se.de.hu_berlin.informatik.utils.tm.pipes.CollectionSequencerPipe;
import se.de.hu_berlin.informatik.utils.tm.pipes.ElementCollectorPipe;
import se.de.hu_berlin.informatik.utils.tm.pipes.ThreadedProcessorPipe;
import se.de.hu_berlin.informatik.utils.tracking.ProgressTracker;
import se.de.hu_berlin.informatik.utils.tracking.TrackingStrategy;

public class EvoAlgorithm<T,L,F> {
	
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
	
	public static enum RecombinationSelectionStrategy {
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
	
	public static enum MutationSelectionStrategy {
		RANDOM
	}
	
	public static enum LocationSelectionStrategy {
		RANDOM
	}
	
public static class Builder<T,L,F> {
		
		private int populationCount;
		private int maxGenerationBound;
		private F fitnessGoal;
		
		private KillStrategy killStrategy;
		private PopulationSelectionStrategy populationSelectionStrategy; 
		private RecombinationSelectionStrategy recombinationSelectionStrategy;
		private RecombinationStrategy recombinationStrategy;
		private EvoLocationProvider<T,L> locationProvider;
		private EvoMutationProvider<T,L> mutationProvider;
		private LocationSelectionStrategy locationSelectionStrategy;
		private MutationSelectionStrategy mutationSelectionStrategy;
		private EvoRecombiner<T> recombiner;
		private PipeLinker evaluationPipe;
		private ElementCollectorPipe<EvoResult<T, F>> collectorPipe;
		
		private List<T> startingPopulation = new ArrayList<>();
		
		public Builder(int populationCount, int maxGenerationBound,
				KillStrategy killStrategy, PopulationSelectionStrategy populationSelectionStrategy) {
			super();
			this.populationCount = populationCount;
			this.maxGenerationBound = maxGenerationBound;
			this.killStrategy = killStrategy;
			this.populationSelectionStrategy = populationSelectionStrategy;
		}
		
		public Builder<T, L, F> setFitnessChecker(EvoHandlerProvider<T, F> evaluationHandlerFactory, int threadCount, F fitnessGoal) {
			this.fitnessGoal = fitnessGoal;
//			this.evaluationHandlerProvider = evaluationHandlerFactory;
			
			this.collectorPipe = new ElementCollectorPipe<EvoResult<T,F>>();
			this.evaluationPipe = new PipeLinker(); 
			this.evaluationPipe.append(
					new CollectionSequencerPipe<T>(),
					new ThreadedProcessorPipe<>(threadCount, evaluationHandlerFactory),
					this.collectorPipe
					);
			
			return this;
		}
		
		public Builder<T, L, F> setLocationProvider(EvoLocationProvider<T,L> locationProvider,
				LocationSelectionStrategy locationSelectionStrategy) {
			this.locationProvider = locationProvider;
			this.locationSelectionStrategy = locationSelectionStrategy;
			return this;
		}
		
		public Builder<T, L, F> setMutationProvider(EvoMutationProvider<T,L> mutationProvider, 
				MutationSelectionStrategy mutationSelectionStrategy) {
			this.mutationProvider = mutationProvider;
			this.mutationSelectionStrategy = mutationSelectionStrategy;
			return this;
		}
		
		public Builder<T, L, F> setRecombiner(EvoRecombiner<T> recombiner,
				RecombinationSelectionStrategy recombinationSelectionStrategy,
				RecombinationStrategy recombinationStrategy) {
			this.recombiner = recombiner;
			this.recombinationSelectionStrategy = recombinationSelectionStrategy;
			this.recombinationStrategy = recombinationStrategy;
			return this;
		}
		
		public EvoAlgorithm<T, L, F> build() {
			return new EvoAlgorithm<T,L,F>(this);
		}
		
		public Builder<T, L, F> addToPopulation(T item) {
			startingPopulation.add(item);
			return this;
		}
		
	}
	
	private final int populationCount;
	private final int maxGenerationBound;
	private final F fitnessGoal;
	
	private final KillStrategy killStrategy;
	private final PopulationSelectionStrategy populationSelectionStrategy; 
	private final RecombinationSelectionStrategy recombinationSelectionStrategy;
	private final RecombinationStrategy recombinationStrategy;
	private final EvoLocationProvider<T,L> locationProvider;
	private final EvoMutationProvider<T,L> mutationProvider;
	private final LocationSelectionStrategy locationSelectionStrategy;
	private final MutationSelectionStrategy mutationSelectionStrategy;
	private final EvoRecombiner<T> recombiner;
	private final PipeLinker evaluationPipe;
	private final ElementCollectorPipe<EvoResult<T, F>> collectorPipe;
	
	private List<T> startingPopulation;
	
	TrackingStrategy tracker = new ProgressTracker(false);
	
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
		
		this.recombiner = builder.recombiner;
		this.recombinationSelectionStrategy = builder.recombinationSelectionStrategy;
		this.recombinationStrategy = builder.recombinationStrategy;
		if (this.recombiner != null) {
			if (this.recombinationSelectionStrategy == null) {
				throw new IllegalStateException("Recombination selection strategy not given.");
			}
			if (this.recombinationStrategy == null) {
				throw new IllegalStateException("Recombination strategy not given.");
			}
		}
		
		this.locationProvider = builder.locationProvider;
		if (this.locationProvider == null) {
			throw new IllegalStateException("Location provider not given.");
		}
		this.locationSelectionStrategy = builder.locationSelectionStrategy;
		if (this.locationSelectionStrategy == null) {
			throw new IllegalStateException("Location selection strategy not given.");
		}
		this.mutationProvider = builder.mutationProvider;
		if (this.mutationProvider == null) {
			throw new IllegalStateException("Mutation provider not given.");
		}
		this.mutationSelectionStrategy = builder.mutationSelectionStrategy;
		if (this.mutationProvider == null) {
			throw new IllegalStateException("Mutation selection strategy not given.");
		}
		
		this.evaluationPipe = builder.evaluationPipe;
		if (this.evaluationPipe == null) {
			throw new IllegalStateException("Evaluation handler (fitness checker) provider not given.");
		}
		this.collectorPipe = builder.collectorPipe;
		if (this.collectorPipe == null) {
			throw new IllegalStateException("Evaluation collector pipe not given.");
		}
		
		this.startingPopulation = builder.startingPopulation;
		if (this.startingPopulation == null || this.startingPopulation.isEmpty()) {
			throw new IllegalStateException("No starting population given.");
		}
	}
	
	public EvoResult<T,F> start() {
		//provide initial item/initial population
		if (startingPopulation.isEmpty()) {
			return null;
		}
		//initialize current population list
		List<T> currentPopulation = new ArrayList<>(startingPopulation);
		
		int generationCounter= 0;
		tracker.track("...running starting generation");
		//fill up with mutants if below desired population size
		currentPopulation.addAll(
				produceMutationBasedOffspring(currentPopulation, populationCount,
						mutationProvider, mutationSelectionStrategy, 
						locationProvider, locationSelectionStrategy));
		
		//test/validate (evaluation)
		List<EvoResult<T, F>> currentEvaluatedPop = 
				calculateFitness(currentPopulation, evaluationPipe, collectorPipe);

		//loop while the generation bound isn't reached and the fitness goal isn't met by any item
		while (generationCounter < maxGenerationBound && !checkIfGoalIsMet(currentEvaluatedPop, fitnessGoal)) {
			++generationCounter;
			tracker.track("...running generation " + generationCounter);
			
			//choose items for new population (selection)
			currentEvaluatedPop = selectNewPopulationAndKillRemaining(currentEvaluatedPop, 
					killStrategy, populationSelectionStrategy, populationCount);
			
			//start a new population
			currentPopulation = new ArrayList<>(populationCount);
			//populate with selected old population
			for (EvoResult<T,F> item : currentEvaluatedPop) {
				currentPopulation.add(item.getItem());
			}
			
			//produce new offspring (recombination) if possible
			if (recombiner != null) {
				//select for recombination
				List<EvoResult<T, F>> parentPopulation = 
						selectForRecombination(currentEvaluatedPop, recombinationSelectionStrategy);
				//cross-over (recombination)
				int childrenCount = populationCount - currentPopulation.size();
				currentPopulation.addAll(
						produceRecombinationalOffspring(parentPopulation, childrenCount, 
								recombinationStrategy, recombiner));
			} 

			//fill up with mutants if below desired population size
			currentPopulation.addAll(
					produceMutationBasedOffspring(currentPopulation, populationCount,
							mutationProvider, mutationSelectionStrategy, 
							locationProvider, locationSelectionStrategy));
			
			//mutate the current population
			currentPopulation = mutatePopulation(currentPopulation, 
					mutationProvider, mutationSelectionStrategy, 
					locationProvider, locationSelectionStrategy);
			
			//test and validate (evaluation)
			currentEvaluatedPop = calculateFitness(currentPopulation, evaluationPipe, collectorPipe);
		} //loop end
		
		//return best item, discard the rest
		return selectBestItemAndCleanUpRest(currentEvaluatedPop);
	}

	public static <T,F> boolean checkIfGoalIsMet(List<EvoResult<T, F>> currentEvaluatedPop, F fitnessGoal) {
		for (EvoResult<T,F> evaluatedItem : currentEvaluatedPop) {
			if (evaluatedItem.compareTo(fitnessGoal) >= 0) {
				return true;
			}
		}
		return false;
	}

	private static <L,T> List<T> produceMutationBasedOffspring(List<T> population, int populationCount,
			EvoMutationProvider<T,L> mutationProvider, MutationSelectionStrategy mutationSelectionStrategy,
			EvoLocationProvider<T,L> locationProvider, LocationSelectionStrategy locationSelectionStrategy) {
		int childrenCount = populationCount - population.size();
		childrenCount = childrenCount < 0 ? 0 : childrenCount;
		if (childrenCount == 0) {
			return Collections.emptyList();
		} else {
			Log.out(EvoAlgorithm.class, "Producing mutation based offspring. (count: %d)", childrenCount);
			List<T> children = new ArrayList<>(childrenCount);

			Collections.shuffle(population);
			int i = 0;
			while (i < childrenCount) {
				//iterate through original population and generate mutants 
				//until the population is filled to the desired count
				Iterator<T> iterator = population.iterator();
				while (iterator.hasNext()) {
					T item = iterator.next();
					children.add(mutationProvider
							.getNextMutation(mutationSelectionStrategy)
							.applyTo(item, locationProvider.getNextLocation(item, locationSelectionStrategy)));
					++i;
				}
			}
			
			return children;
		}
	}

	private EvoResult<T, F> selectBestItemAndCleanUpRest(List<EvoResult<T, F>> currentEvaluatedPop) {
		if (currentEvaluatedPop.isEmpty()) {
			return null;
		}
		EvoResult<T,F> bestItem = currentEvaluatedPop.iterator().next();
		for (EvoResult<T,F> evaluatedItem : currentEvaluatedPop) {
			if (evaluatedItem.compareTo(bestItem.getFitness()) > 0) {
				bestItem = evaluatedItem;
			}
		}
		cleanUpOtherItems(currentEvaluatedPop, bestItem);
		return bestItem;
	}

	private static <L,T> List<T> mutatePopulation(List<T> currentPopulation, 
			EvoMutationProvider<T,L> mutationProvider, MutationSelectionStrategy mutationSelectionStrategy,
			EvoLocationProvider<T,L> locationProvider, LocationSelectionStrategy locationSelectionStrategy) {
		Log.out(EvoAlgorithm.class, "Mutating %d elements.", currentPopulation.size());
		List<T> mutatedPopulation = new ArrayList<>();
		for (T item : currentPopulation) {
			mutatedPopulation.add(mutationProvider
					.getNextMutation(mutationSelectionStrategy)
					.applyTo(item, locationProvider.getNextLocation(item, locationSelectionStrategy)));
		}
		return mutatedPopulation;
	}

	private static <T,F> List<T> produceRecombinationalOffspring(List<EvoResult<T, F>> parentPopulation, int childrenCount,
			RecombinationStrategy recombinationStrategy, EvoRecombiner<T> recombiner) {
		if (parentPopulation.size() < 2) {
			Log.warn(EvoAlgorithm.class, "Need at least 2 parents to produce offspring!");
			return Collections.emptyList();
		} else {
			Log.out(EvoAlgorithm.class, "Producing recombination based offspring. (count: %d)", childrenCount);
			List<T> children = new ArrayList<>();

			switch (recombinationStrategy) {
			case MONOGAMY_BEST_TO_WORST:
				sortBestToWorst(parentPopulation);
				monogamyChildrenProduction(parentPopulation, childrenCount, recombiner, children);
				break;
			case MONOGAMY_RANDOM:
				Collections.shuffle(parentPopulation);
				monogamyChildrenProduction(parentPopulation, childrenCount, recombiner, children);
				break;
			case POLYGAMY_BEST_5_PERCENT_WITH_OTHERS:
				polygamyChildrenProduction(parentPopulation, childrenCount, recombiner, children, parentPopulation.size() * 0.05);
				break;
			case POLYGAMY_BEST_10_PERCENT_WITH_OTHERS:
				polygamyChildrenProduction(parentPopulation, childrenCount, recombiner, children, parentPopulation.size() * 0.1);
				break;
			case POLYGAMY_BEST_20_PERCENT_WITH_OTHERS:
				polygamyChildrenProduction(parentPopulation, childrenCount, recombiner, children, parentPopulation.size() * 0.2);
				break;
			case POLYGAMY_BEST_50_PERCENT_WITH_OTHERS:
				polygamyChildrenProduction(parentPopulation, childrenCount, recombiner, children, parentPopulation.size() * 0.5);
				break;
			case POLYGAMY_SINGLE_BEST_WITH_OTHERS:
				polygamyChildrenProduction(parentPopulation, childrenCount, recombiner, children, 1);
				break;
			default:
				throw new UnsupportedOperationException("Not implemented, yet.");
			}

			//maybe, we have to remove some children now...
			removeRandomlyToSize(children, childrenCount);

			return children;
		}
	}

	private static <T> void removeRandomlyToSize(List<T> list, int maxNumberOfElements) {
		//TODO: clean up needed here?
		if (list.size() > maxNumberOfElements) {
			//remove randomly
			Collections.shuffle(list);
			int killCount = list.size() - maxNumberOfElements;
			Iterator<T> iterator = list.iterator();
			int i = 0;
			while (iterator.hasNext() && i < killCount) {
				iterator.next();
				iterator.remove();
				++i;
			}
			//R.I.P.
		}
	}

	private static <T,F> void polygamyChildrenProduction(List<EvoResult<T, F>> parentPopulation, int childrenCount,
			EvoRecombiner<T> recombiner, List<T> children, double number) {
		List<EvoResult<T, F>> bestParents = new ArrayList<>();
		List<EvoResult<T, F>> population = new ArrayList<>(parentPopulation);
		sortBestToWorst(population);
		transferNumberOfFirstItemsToCollector(population, number, 1, bestParents);
		Log.out(EvoAlgorithm.class, "Polygamy... strong parent count: %d, weak parent count: %d", bestParents.size(), population.size());
		int i = 0;
		EvoResult<T, F> parent1 = null;
		EvoResult<T, F> parent2 = null;
		//repeat until all children are generated
		while (i < childrenCount) {
			//start iterating over the best parents
			Iterator<EvoResult<T,F>> iterator = bestParents.iterator();
			while (iterator.hasNext()) {
				//get parent1
				parent1 = iterator.next();
				
				//start iterating over the other parents
				Iterator<EvoResult<T,F>> iterator2 = population.iterator();
				while (iterator2.hasNext()) {
					//get parent2
					parent2 = iterator2.next();

					//if both parents are picked, produce a child and reset the parents
					children.add(recombiner.recombine(parent1.getItem(), parent2.getItem()));
					++i;
				}
			}
			
		}
	}

	private static <T,F> void monogamyChildrenProduction(List<EvoResult<T, F>> parentPopulation, int childrenCount,
			EvoRecombiner<T> recombiner, List<T> children) {
		Log.out(EvoAlgorithm.class, "Monogamy... parent count: %d", parentPopulation.size());
		int i = 0;
		EvoResult<T, F> parent1 = null;
		EvoResult<T, F> parent2 = null;
		//repeat until all children are generated
		while (i < childrenCount) {
			//start iterating over the parents
			Iterator<EvoResult<T,F>> iterator = parentPopulation.iterator();
			while (iterator.hasNext()) {
				//first get parent1, then parent2
				if (parent1 == null) {
					parent1 = iterator.next();
				} else if (parent2 == null) {
					parent2 = iterator.next();
				} 
				
				//if both parents are picked, produce a child and reset the parents
				if (parent1 != null && parent2 != null) {
					children.add(recombiner.recombine(parent1.getItem(), parent2.getItem()));
					++i;
					parent1 = null;
					parent2 = null;
				}
			}
		}
	}
	
	private static <T,F> List<EvoResult<T, F>> selectForRecombination(List<EvoResult<T, F>> evaluatedPop,
			RecombinationSelectionStrategy recombinationSelectionStrategy) {
		Log.out(EvoAlgorithm.class, "Selecting parents from %d elements.", evaluatedPop.size());
		if (evaluatedPop.size() < 2) {
			Log.warn(EvoAlgorithm.class, "Population size too small to select enough parents!");
			return Collections.emptyList();
		} else {
			double bestPercentage = 0;
			double randomPercentage = 0;
			double worstPercentage = 0;

			switch (recombinationSelectionStrategy) {
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
	
	private static <T,F> List<EvoResult<T, F>> selectForRecombination(List<EvoResult<T, F>> evaluatedPop,
			double bestNumber, double randomNumber, double worstNumber) {
		List<EvoResult<T, F>> parents = new ArrayList<>();
		List<EvoResult<T, F>> population = new ArrayList<>(evaluatedPop);
		
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

	private static <T,F> void sortWorstToBest(List<EvoResult<T, F>> evaluatedPop) {
		//sort from smallest to biggest (worst to best)
		evaluatedPop.sort((o1,o2) -> o1.compareTo(o2.getFitness()));
	}

	private static <T,F> void sortBestToWorst(List<EvoResult<T, F>> evaluatedPop) {
		//sort from biggest to smallest (best to worst)
		evaluatedPop.sort((o1,o2) -> o2.compareTo(o1.getFitness()));
	}

	private static <T,F> void transferNumberOfFirstItemsToCollector(
			List<EvoResult<T, F>> sourcePopulation, double numberOfItems, int leastAmount, List<EvoResult<T, F>> collector) {
		//transfer at least 'leastAmount' items
		int transferCount = (int)numberOfItems > leastAmount ? (int)numberOfItems : leastAmount;
		int i = 0;
		Iterator<EvoResult<T,F>> iterator = sourcePopulation.iterator();
		while (iterator.hasNext() && i < transferCount) {
			++i;
			collector.add(iterator.next());
			iterator.remove();
		}
	}

	private static <T,F> List<EvoResult<T, F>> selectNewPopulationAndKillRemaining(List<EvoResult<T, F>> evaluatedPop,
			KillStrategy killStrategy, PopulationSelectionStrategy populationSelectionStrategy, int populationCount) {
		//kill off items to only keep half of the maximal population //TODO: other strategies...
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
		Log.out(EvoAlgorithm.class, "Selecting %d from %d elements.", (int)selectionCount, evaluatedPop.size());
		List<EvoResult<T, F>> resultPopulation = new ArrayList<>((int)selectionCount);

		switch (populationSelectionStrategy) {
		case BEST_ONLY:
			sortBestToWorst(evaluatedPop);
			transferNumberOfFirstItemsToCollector(evaluatedPop, selectionCount, 1, resultPopulation);
			break;
		case HALF_BEST_HALF_RANDOM:
			sortBestToWorst(evaluatedPop);
			transferNumberOfFirstItemsToCollector(evaluatedPop, selectionCount * 0.5, 1, resultPopulation);
			Collections.shuffle(evaluatedPop);
			transferNumberOfFirstItemsToCollector(evaluatedPop, selectionCount * 0.5, 1, resultPopulation);
			break;
		case RANDOM:
			Collections.shuffle(evaluatedPop);
			transferNumberOfFirstItemsToCollector(evaluatedPop, selectionCount, 1, resultPopulation);
			break;
		default:
			throw new UnsupportedOperationException("Not implemented, yet.");
		}
		
		//discard the remaining items
		cleanUpAllItems(evaluatedPop);
		
		//maybe remove overpopulation that got through mistakenly
		removeRandomlyToSizeAndCleanUp(resultPopulation, (int)selectionCount);

		return resultPopulation;
	}
	
	private static <T,F> void removeRandomlyToSizeAndCleanUp(List<EvoResult<T, F>> list, int maxNumberOfElements) {
		if (list.size() > maxNumberOfElements) {
			//remove randomly
			Collections.shuffle(list);
			int killCount = list.size() - maxNumberOfElements;
			Iterator<EvoResult<T, F>> iterator = list.iterator();
			int i = 0;
			while (iterator.hasNext() && i < killCount) {
				iterator.next();
				iterator.remove();
				++i;
			}
			//R.I.P.
		}
	}

	private static <T,F> void cleanUpOtherItems(List<EvoResult<T, F>> evaluatedPop, EvoResult<T, F> evaluatedItem) {
		for (EvoResult<T,F> item : evaluatedPop) {
			if (evaluatedItem != item) {
				item.cleanUp();
			}
		}
	}
	
	private static <T,F> void cleanUpAllItems(List<EvoResult<T, F>> list) {
		for (EvoResult<T,F> item : list) {
			item.cleanUp();
		}
	}

	private static <T,F> List<EvoResult<T, F>> calculateFitness(List<T> population,
			PipeLinker evaluationPipe, ElementCollectorPipe<EvoResult<T,F>> collectorPipe) {
		Log.out(EvoAlgorithm.class, "Checking fitness for %d elements.", population.size());
		//check all elements in the population for their fitness values
		evaluationPipe.submitAndShutdown(population);
		
		//return a list with the evaluated population
		return collectorPipe.getCollectedItems();
	}

}
