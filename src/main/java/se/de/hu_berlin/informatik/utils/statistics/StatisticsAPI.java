package se.de.hu_berlin.informatik.utils.statistics;

public interface StatisticsAPI {
	
	public static enum StatisticType {
		BOOLEAN,
		INTEGER,
		DOUBLE,
		STRING
	}
	
	public String getLabel();
	
	public StatisticType getType();
	
	public StatisticsOptions[] getOptions();

}
