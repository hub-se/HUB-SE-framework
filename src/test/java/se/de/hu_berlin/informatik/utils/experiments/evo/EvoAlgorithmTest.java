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
import se.de.hu_berlin.informatik.utils.statistics.StatisticsCollector;

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
				if (item.length > 0) {
					return random.nextInt(item.length);
				} else {
					return 0;
				}
			}
		};
		
		EvoMutation<Integer[],Integer,Integer> mutation = new EvoMutation<Integer[],Integer,Integer>() {
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
			public EvoID<Integer> getIDofNextMutation(Integer[] target, Integer location) {
				nextGaussian = random.nextGaussian();
				return new EvoID<>(0, nextGaussian >= 0 ? location : -location);
			}
		};
		
		EvoMutation<Integer[],Integer,Integer> mutationLength = new EvoMutation<Integer[],Integer,Integer>() {
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
			public EvoID<Integer> getIDofNextMutation(Integer[] target, Integer location) {
				if (target.length > 1) {
					nextGaussian = random.nextGaussian();
					return new EvoID<>(1, nextGaussian >= 0 ? 1 : -1);
				} else {
					nextGaussian = 1;
					return new EvoID<>(1, 1);
				}
			}
		};
		
		EvoFitnessChecker<Integer[],Integer,Integer> fitnessChecker = new EvoFitnessChecker<Integer[],Integer,Integer>() {
			
			@Override
			public Integer computeFitness(Integer[] item) {
				//goal: 0 (at the moment holds: the bigger number, the greater the fitness...)
				int fitness = 0;
				if (item.length != goal.length) {
					fitness = (int) -Math.pow(Math.abs(item.length - goal.length) * 10, 2);
				} else {
					for (int i = 0; i < item.length; ++i) {
						fitness -= Math.pow(Math.abs(goal[i] - item[i]), 2);
					}
				}

				return fitness;
			}
		};
		
		//recombiner is optional
		EvoRecombination<Integer[], Integer> recombination = new EvoRecombination<Integer[], Integer>() {
			
			private double nextGaussian;
			private int switchIndex;

			@Override
			public Integer[] recombine(Integer[] parent1, Integer[] parent2) {
				Integer[] child;
				
				if (nextGaussian >= 0) {
					child = new Integer[parent2.length];
					for (int i = 0; i < switchIndex; ++i) {
						child[i] = parent1[i];
					}
					for (int i = 0; i < parent2.length; ++i) {
						child[i] = parent2[i];
					}
				} else {
					child = new Integer[parent1.length];
					for (int i = 0; i < switchIndex; ++i) {
						child[i] = parent2[i];
					}
					for (int i = 0; i < parent1.length; ++i) {
						child[i] = parent1[i];
					}
				}
				return child;
			}
			
			@Override
			public EvoID<Integer> getIDofNextRecombination(Integer[] parent1, Integer[] parent2) {
				int smallerLength = parent1.length < parent2.length ? parent1.length : parent2.length;
				switchIndex = random.nextInt(smallerLength-1) + 1;
				nextGaussian = random.nextGaussian();
				return new EvoID<>(0, nextGaussian >= 0 ? switchIndex : -switchIndex);
			}
		};
		
		StatisticsCollector<EvoStatistics> collector = new StatisticsCollector<>(EvoStatistics.class);
		
		EvoAlgorithm.Builder<Integer[], Integer, Integer, Integer> builder = 
				new EvoAlgorithm.Builder<Integer[], Integer, Integer, Integer>(50, 20, 
						KillStrategy.KILL_50_PERCENT, 
						PopulationSelectionStrategy.HALF_BEST_HALF_RANDOM, 
						ParentSelectionStrategy.BEST_75_PERCENT,
						RecombinationStrategy.POLYGAMY_BEST_20_PERCENT_WITH_OTHERS)
//						RecombinationStrategy.MONOGAMY_BEST_TO_WORST)
//				.useHistory()
				.addRecombinationTemplate(recombination)
				.addMutationTemplate(mutation)
				.addMutationTemplate(mutationLength)
				.setLocationProvider(locationProvider)
				.setFitnessChecker(fitnessChecker, 4, 0)
				.setStatisticsCollector(collector)
				.addToInitialPopulation(new Integer[] {2,4,1,0})
				.addToInitialPopulation(new Integer[] {0,2,10});
		
		EvoItem<Integer[],Integer,Integer> result = builder.build().start();
		
		Log.out(this, collector.printStatistics());
		
		Log.out(this, "result: %s", Misc.arrayToString(result.getItem()));
		Log.out(this, "fitness: %d", result.getFitness().intValue());

	}

	

}
