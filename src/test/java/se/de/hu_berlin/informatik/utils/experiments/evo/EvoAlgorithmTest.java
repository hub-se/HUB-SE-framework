/**
 * 
 */
package se.de.hu_berlin.informatik.utils.experiments.evo;

import java.util.Collection;
import java.util.Random;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.de.hu_berlin.informatik.utils.experiments.evo.EvoHandlerProvider;
import se.de.hu_berlin.informatik.utils.experiments.evo.EvoLocationProvider;
import se.de.hu_berlin.informatik.utils.experiments.evo.EvoMutation;
import se.de.hu_berlin.informatik.utils.experiments.evo.EvoMutationProvider;
import se.de.hu_berlin.informatik.utils.experiments.evo.EvoItem;
import se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm;
import se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.KillStrategy;
import se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.LocationSelectionStrategy;
import se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.MutationSelectionStrategy;
import se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.PopulationSelectionStrategy;
import se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.RecombinationParentSelectionStrategy;
import se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.RecombinationStrategy;
import se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.RecombinationTypeSelectionStrategy;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;
import se.de.hu_berlin.informatik.utils.miscellaneous.TestSettings;

/**
 * @author Simon
 *
 */
public class EvoAlgorithmTest extends TestSettings {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		deleteTestOutputs();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		deleteTestOutputs();
	}

	/**
	 * Test method for {@link se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm#EvolutionaryAlgorithm(int, int, int, java.lang.Object, se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.PopulationSelectionStrategy, se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.RecombinationParentSelectionStrategy, se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.RecombinationStrategy, se.de.hu_berlin.informatik.utils.experiments.evo.EvoLocationProvider, se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.LocationSelectionStrategy, se.de.hu_berlin.informatik.utils.experiments.evo.EvoMutationProvider, se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.MutationSelectionStrategy, se.de.hu_berlin.informatik.utils.experiments.evo.EvoRecombination, se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInputAndReturnFactory)}.
	 */
	@Test
	public void testEvolutionaryAlgorithm() throws Exception {
		Integer[] goal = { 1, 2, 3 };
		Random random = new Random(123456789);
		
		EvoLocationProvider<Integer[],Integer,Integer> locationProvider = new EvoLocationProvider<Integer[],Integer,Integer>() {
			@Override
			public Integer getNextLocation(Integer[] item, LocationSelectionStrategy strategy) {
				return random.nextInt(item.length);
			}
		};
		
		EvoMutationProvider<Integer[],Integer> mutationProvider = new EvoMutationProvider<Integer[],Integer>() {
			@Override
			public EvoMutation<Integer[],Integer> getNextMutationType(MutationSelectionStrategy strategy) {
				return new EvoMutation<Integer[],Integer>() {
					double nextGaussian = 0;
					@Override
					public Integer[] applyTo(Integer[] target, Integer location) {
						Integer[] array = new Integer[target.length];
						for (int i = 0; i < array.length; ++i) {
							if (i == location) {
								if (nextGaussian >= 0) {
									array[i] = target[i] + 1;
								} else {
									array[i] = target[i] - 1;
								}
							} else {
								array[i] = target[i];
							}
						}
						return array;
					}

					@Override
					public int getIDofNextMutation(Integer location) {
						nextGaussian = random.nextGaussian();
						return nextGaussian >= 0 ? location : -location;
					}
				};
			}

			@Override
			public boolean addMutation(EvoMutation<Integer[], Integer> mutationFunction) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Collection<EvoMutation<Integer[], Integer>> getMutations() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		EvoHandlerProvider<Integer[],Integer> evaluationHandlerFactory = new EvoHandlerProvider<Integer[],Integer>() {
			
			@Override
			public Integer computeFitness(Integer[] item) {
				int fitness = 0;
				if (item.length != goal.length) {
					fitness = Integer.MAX_VALUE;
				} else {
					for (int i = 0; i < item.length; ++i) {
						fitness += Math.pow(Math.abs(goal[i] - item[i]), 2);
					}
				}

				return fitness;
			}
		};
		
		//recombiner is optional
		EvoRecombinationProvider<Integer[]> recombiner = new EvoRecombinationProvider<Integer[]>() {

			@Override
			public EvoRecombination<Integer[]> getNextRecombinationType(RecombinationTypeSelectionStrategy strategy) {
				return new EvoRecombination<Integer[]>() {
					
					private double nextGaussian;
					private int switchIndex;

					@Override
					public Integer[] recombine(Integer[] parent1, Integer[] parent2) {
						Integer[] child = new Integer[parent1.length];
						
						if (nextGaussian >= 0) {
							for (int i = 0; i < child.length; ++i) {
								if (i < switchIndex) {
									child[i] = parent1[i];
								} else {
									child[i] = parent2[i];
								}
							}
						} else {
							for (int i = 0; i < child.length; ++i) {
								if (i < switchIndex) {
									child[i] = parent2[i];
								} else {
									child[i] = parent1[i];
								}
							}
						}
						return child;
					}
					
					@Override
					public int getIDofNextRecombination(Integer[] parent1, Integer[] parent2) {
						switchIndex = random.nextInt(parent1.length-1) + 1;
						nextGaussian = random.nextGaussian();
						return nextGaussian >= 0 ? switchIndex : -switchIndex;
					}
				};
			}

			@Override
			public boolean addRecombination(EvoRecombination<Integer[]> recombination) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Collection<EvoRecombination<Integer[]>> getRecombinations() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		EvoAlgorithm.Builder<Integer[], Integer, Integer> builder = 
				new EvoAlgorithm.Builder<Integer[], Integer, Integer>(50, 20, KillStrategy.KILL_75_PERCENT, PopulationSelectionStrategy.BEST_ONLY)
				.setRecombinationProvider(recombiner, RecombinationTypeSelectionStrategy.RANDOM, RecombinationParentSelectionStrategy.BEST_75_PERCENT,
//						RecombinationStrategy.MONOGAMY_BEST_TO_WORST)
						RecombinationStrategy.POLYGAMY_BEST_20_PERCENT_WITH_OTHERS)
				.setMutationProvider(mutationProvider, MutationSelectionStrategy.RANDOM)
				.setLocationProvider(locationProvider, LocationSelectionStrategy.RANDOM)
				.setFitnessChecker(evaluationHandlerFactory, 4, 0)
				.addToPopulation(new Integer[] {2,4,1});
		
		EvoItem<Integer[],Integer> result = builder.build().start();
		
		Log.out(this, "result: %s", Misc.arrayToString(result.getItem()));
		Log.out(this, "fitness: %d", result.getFitness().intValue());
	}


}
