package se.de.hu_berlin.informatik.utils.statistics;

import se.de.hu_berlin.informatik.utils.statistics.StatisticsAPI.StatisticType;

public interface StatisticsElementCollector {

	public StatisticType getType();
	
	public boolean addElement(StatisticsElement<?> element);
	
}
