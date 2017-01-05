package se.de.hu_berlin.informatik.utils.statistics;

public interface StatisticsAPI {
	
	public static enum StatisticType {
		BOOLEAN,
		COUNT,
		INTEGER_VALUE,
		DOUBLE_VALUE,
		STRING
	}
	
	public String getLabel();
	
	public StatisticType getType();
	
	public StatisticsOptions[] getOptions();

}
