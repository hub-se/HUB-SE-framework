package se.de.hu_berlin.informatik.utils.statistics;

import se.de.hu_berlin.informatik.utils.statistics.StatisticsAPI.StatisticType;

public interface StatisticsElementList {

	public StatisticType getType();
	
	public boolean addElement(StatisticsElement<?> element);
	
}
