package se.de.hu_berlin.informatik.utils.statistics;

import se.de.hu_berlin.informatik.utils.statistics.Statistics.StatisticType;

public abstract class AbstractStatisticsElementList implements StatisticsElementList {

	private Statistics.StatisticType type;
	
	public AbstractStatisticsElementList(StatisticType type) {
		super();
		this.type = type;
	}

	public Statistics.StatisticType getType() {
		return type;
	}
	
}
