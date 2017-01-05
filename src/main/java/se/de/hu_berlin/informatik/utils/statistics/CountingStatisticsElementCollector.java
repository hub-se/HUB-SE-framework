package se.de.hu_berlin.informatik.utils.statistics;

import se.de.hu_berlin.informatik.utils.statistics.StatisticsAPI.StatisticType;

public class CountingStatisticsElementCollector extends AbstractStatisticsElementCollector {
	
	private long elementCount = 0;
	
	public CountingStatisticsElementCollector() {
		super(StatisticType.COUNT);
	}
	
	@Override
	public boolean addElement(StatisticsElement<?> element) {
		if (element.getType() != this.getType()) {
			return false;
		}
		elementCount += (Integer)element.getValue();
		return true;
	}
	
	public long getElementCount() {
		return elementCount;
	}
	
}
