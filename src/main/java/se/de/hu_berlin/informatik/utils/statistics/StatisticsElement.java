package se.de.hu_berlin.informatik.utils.statistics;

public interface StatisticsElement<T> {

	public Statistics.StatisticType getType();
	
	public T getValue();
	
	public void setValue(T value);

	public void mergeWith(StatisticsElement<?> element);
	
}
