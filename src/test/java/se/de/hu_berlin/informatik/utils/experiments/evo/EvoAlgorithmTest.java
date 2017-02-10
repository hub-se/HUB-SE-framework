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
import se.de.hu_berlin.informatik.utils.experiments.evo.EvoResult;
import se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm;
import se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.KillStrategy;
import se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.LocationSelectionStrategy;
import se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.MutationSelectionStrategy;
import se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.PopulationSelectionStrategy;
import se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.RecombinationSelectionStrategy;
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
	 * Test method for {@link se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm#EvolutionaryAlgorithm(int, int, int, java.lang.Object, se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.PopulationSelectionStrategy, se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.RecombinationSelectionStrategy, se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.RecombinationStrategy, se.de.hu_berlin.informatik.utils.experiments.evo.EvoLocationProvider, se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.LocationSelectionStrategy, se.de.hu_berlin.informatik.utils.experiments.evo.EvoMutationProvider, se.de.hu_berlin.informatik.utils.experiments.evo.EvoAlgorithm.MutationSelectionStrategy, se.de.hu_berlin.informatik.utils.experiments.evo.EvoRecombiner, se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInputAndReturnFactory)}.
	 */
	@Test
	public void testEvolutionaryAlgorithm() throws Exception {
		Integer[] goal = { 1, 2, 3 };
		Random random = new Random(123456789);
		
		EvoLocationProvider<Integer[],Integer> locationProvider = new EvoLocationProvider<Integer[],Integer>() {
			@Override
			public Integer getNextLocation(Integer[] item, LocationSelectionStrategy strategy) {
				return random.nextInt(item.length);
			}
		};
		
		EvoMutationProvider<Integer[],Integer> mutationProvider = new EvoMutationProvider<Integer[],Integer>() {
			@Override
			public EvoMutation<Integer[], Integer> getNextMutation(MutationSelectionStrategy strategy) {
				return new EvoMutation<Integer[],Integer>() {
					@Override
					public Integer[] applyTo(Integer[] target, Integer location) {
						Integer[] array = new Integer[target.length];
						for (int i = 0; i < array.length; ++i) {
							if (i == location) {
								if (random.nextGaussian() >= 0) {
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
			public EvoResult<Integer[], Integer> computeFitness(Integer[] item) {
				int fitness = 0;
				if (item.length != goal.length) {
					fitness = Integer.MAX_VALUE;
				} else {
					for (int i = 0; i < item.length; ++i) {
						fitness += Math.pow(Math.abs(goal[i] - item[i]), 2);
					}
				}

				return new TestEvoResult(item, fitness);
			}
		};
		
		//recombiner is optional
		EvoRecombiner<Integer[]> recombiner = new EvoRecombiner<Integer[]>() {
			
			@Override
			public Integer[] recombine(Integer[] parent1, Integer[] parent2) {
				int switchIndex = random.nextInt(parent1.length-1) + 1;
				Integer[] child = new Integer[parent1.length];
				
				if (random.nextGaussian() >= 0) {
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
		};
		
		EvoAlgorithm.Builder<Integer[], Integer, Integer> builder = 
				new EvoAlgorithm.Builder<Integer[], Integer, Integer>(50, 20, KillStrategy.KILL_75_PERCENT, PopulationSelectionStrategy.BEST_ONLY)
				.setRecombiner(recombiner , RecombinationSelectionStrategy.BEST_75_PERCENT,
//						RecombinationStrategy.MONOGAMY_BEST_TO_WORST)
						RecombinationStrategy.POLYGAMY_BEST_20_PERCENT_WITH_OTHERS)
				.setMutationProvider(mutationProvider, MutationSelectionStrategy.RANDOM)
				.setLocationProvider(locationProvider, LocationSelectionStrategy.RANDOM)
				.setFitnessChecker(evaluationHandlerFactory, 4, 0)
				.addToPopulation(new Integer[] {2,4,1});
		
		EvoResult<Integer[],Integer> result = builder.build().start();
		
		Log.out(this, "result: %s", Misc.arrayToString(result.getItem()));
		Log.out(this, "fitness: %d", result.getFitness().intValue());
	}
	
	private static class TestEvoResult implements EvoResult<Integer[], Integer> {

		private int fitness;
		private Integer[] item;
		
		public TestEvoResult(Integer[] item, int fitness) {
			this.item = item;
			this.fitness = fitness;
		}
		
		@Override
		public int compareTo(Integer o) {
			return o.compareTo(fitness);
		}

		@Override
		public Integer getFitness() {
			return fitness;
		}

		@Override
		public Integer[] getItem() {
			return item;
		}

		@Override
		public boolean cleanUp() {
			item = null;
			return true;
		}
		
	}

}
