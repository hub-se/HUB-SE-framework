package se.de.hu_berlin.informatik.utils.statistics;

import se.de.hu_berlin.informatik.utils.statistics.StatisticsAPI.StatisticType;

public abstract class AbstractStatisticsElementList implements StatisticsElementList {

	private StatisticType type;
	
	public AbstractStatisticsElementList(StatisticType type) {
		super();
		this.type = type;
	}

	public StatisticType getType() {
		return type;
	}
	
}
