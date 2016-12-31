package se.de.hu_berlin.informatik.utils.statistics;

public interface StatisticsElementList {

	public Statistics.StatisticType getType();
	
	public boolean addElement(StatisticsElement<?> element);
	
}
