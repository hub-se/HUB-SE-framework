package se.de.hu_berlin.informatik.utils.experiments.evo;

import se.de.hu_berlin.informatik.utils.statistics.StatisticsAPI;
import se.de.hu_berlin.informatik.utils.statistics.StatisticsOptions;

public enum EvoStatistics implements StatisticsAPI {
	MAX_GENERATION_COUNT("max generation count", StatisticType.COUNT, StatisticsOptions.PREF_BIGGER),
	POPULATION_SIZE("population size", StatisticType.COUNT, StatisticsOptions.PREF_BIGGER),
	GENERATION_COUNT("final generation count", StatisticType.COUNT, StatisticsOptions.PREF_BIGGER),
	EVALUATED_ELEMENTS_COUNT("elements evaluated", StatisticType.COUNT, StatisticsOptions.ADD),
	DUPLICATE_ELEMENTS_COUNT("duplicate elements discarded", StatisticType.COUNT, StatisticsOptions.ADD),
	BEST_FITNESS_REPRESENTATIONS("best fitness values per generation", StatisticType.STRING, StatisticsOptions.PREF_NEW);

	final private String label;
	final private StatisticType type;
	final private StatisticsOptions[] options;
	private EvoStatistics(String label, StatisticType type, StatisticsOptions... options) {
		this.label = label;
		this.type = type;
		this.options = options;
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public StatisticType getType() {
		return type;
	}

	@Override
	public StatisticsOptions[] getOptions() {
		return options;
	}
}
