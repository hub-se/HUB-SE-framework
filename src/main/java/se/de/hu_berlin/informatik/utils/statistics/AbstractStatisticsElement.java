package se.de.hu_berlin.informatik.utils.statistics;

import se.de.hu_berlin.informatik.utils.statistics.StatisticsAPI.StatisticType;

public abstract class AbstractStatisticsElement<T> implements StatisticsElement<T> {

	private StatisticType type;
	
	public AbstractStatisticsElement(StatisticType type) {
		super();
		this.type = type;
	}

	public StatisticType getType() {
		return type;
	}
	
}
