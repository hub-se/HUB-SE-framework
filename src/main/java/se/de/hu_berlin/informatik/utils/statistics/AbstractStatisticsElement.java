package se.de.hu_berlin.informatik.utils.statistics;

import se.de.hu_berlin.informatik.utils.statistics.Statistics.StatisticType;

public abstract class AbstractStatisticsElement<T> implements StatisticsElement<T> {

	private Statistics.StatisticType type;
	
	public AbstractStatisticsElement(StatisticType type) {
		super();
		this.type = type;
	}

	public Statistics.StatisticType getType() {
		return type;
	}
	
}
