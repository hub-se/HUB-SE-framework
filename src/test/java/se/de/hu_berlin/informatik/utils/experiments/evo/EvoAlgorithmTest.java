/**
 * 
 */
package se.de.hu_berlin.informatik.utils.experiments.evo;

import java.util.Random;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.de.hu_berlin.informatik.utils.experiments.evo.EvoFitnessChecker;
import se.de.hu_berlin.informatik.utils.experiments.evo.EvoLocationProvider;
import se.de.hu_berlin.informatik.utils.experiments.evo.EvoMutation;
import se.de.hu_berlin.informatik.utils.experiments.evo.EvoItem;
import se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm;
import se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.KillStrategy;
import se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.PopulationSelectionStrategy;
import se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.ParentSelectionStrategy;
import se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.RecombinationStrategy;
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
	 * Test method for {@link se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm#EvolutionaryAlgorithm(int, int, int, java.lang.Object, se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.PopulationSelectionStrategy, se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.ParentSelectionStrategy, se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.RecombinationStrategy, se.de.hu_berlin.informatik.utils.experiments.evo.EvoLocationProvider, se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.LocationSelectionStrategy, se.de.hu_berlin.informatik.utils.experiments.evo.EvoMutationProvider, se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.MutationSelectionStrategy, se.de.hu_berlin.informatik.utils.experiments.evo.EvoRecombination, se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInputAndReturnFactory)}.
	 */
	@Test
	public void testEvolutionaryAlgorithm() throws Exception {
		Integer[] goal = { 1, 2, 3 };
		Random random = new Random(123456789);
		
		EvoLocationProvider<Integer[],Integer> locationProvider = new AbstractEvoLocationProvider<Integer[],Integer>() {
			@Override
			public Integer getNextLocation(Integer[] item, LocationSelectionStrategy strategy) {
				return random.nextInt(item.length);
			}
		};
		
		EvoMutation<Integer[],Integer> mutation = new EvoMutation<Integer[],Integer>() {
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
			public int getIDofNextMutation(Integer[] target, Integer location) {
				nextGaussian = random.nextGaussian();
				return nextGaussian >= 0 ? location : -location;
			}
		};
		
		EvoMutation<Integer[],Integer> mutationLength = new EvoMutation<Integer[],Integer>() {
			double nextGaussian = 0;
			@Override
			public Integer[] applyTo(Integer[] target, Integer location) {
				Integer[] array;
				if (nextGaussian >= 0) {
					array = new Integer[target.length+1];
					System.arraycopy(target, 0, array, 0, target.length);
					array[target.length] = 0;
				} else {
					array = new Integer[target.length-1];
					System.arraycopy(target, 0, array, 0, target.length-1);
				}
				return array;
			}

			@Override
			public int getIDofNextMutation(Integer[] target, Integer location) {
				nextGaussian = random.nextGaussian();
				return nextGaussian >= 0 ? location : -location;
			}
		};
		
		EvoFitnessChecker<Integer[],Integer> fitnessChecker = new EvoFitnessChecker<Integer[],Integer>() {
			
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
		EvoRecombination<Integer[]> recombination = new EvoRecombination<Integer[]>() {
			
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
		
		EvoAlgorithm.Builder<Integer[], Integer, Integer> builder = 
				new EvoAlgorithm.Builder<Integer[], Integer, Integer>(50, 20, 
						KillStrategy.KILL_50_PERCENT, 
						PopulationSelectionStrategy.HALF_BEST_HALF_RANDOM, 
						ParentSelectionStrategy.BEST_75_PERCENT,
						RecombinationStrategy.POLYGAMY_BEST_20_PERCENT_WITH_OTHERS)
//						RecombinationStrategy.MONOGAMY_BEST_TO_WORST)
				.addRecombinationTemplate(recombination)
				.addMutationTemplate(mutation)
				.setLocationProvider(locationProvider)
				.setFitnessChecker(fitnessChecker, 4, 0)
				.addToInitialPopulation(new Integer[] {2,4,1});
		
		EvoItem<Integer[],Integer> result = builder.build().start();
		
		Log.out(this, "result: %s", Misc.arrayToString(result.getItem()));
		Log.out(this, "fitness: %d", result.getFitness().intValue());
	}


}
