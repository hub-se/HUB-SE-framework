package se.de.hu_berlin.informatik.utils.statistics;

import se.de.hu_berlin.informatik.utils.statistics.StatisticsAPI.StatisticType;

public abstract class AbstractStatisticsElementCollector implements StatisticsElementCollector {

	private StatisticType type;
	
	public AbstractStatisticsElementCollector(StatisticType type) {
		super();
		this.type = type;
	}

	public StatisticType getType() {
		return type;
	}
	
}
