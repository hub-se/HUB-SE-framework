package se.de.hu_berlin.informatik.utils.statistics;

import se.de.hu_berlin.informatik.utils.statistics.Statistics.StatisticType;

public abstract class AbstractIntegerStatisticsElement extends AbstractStatisticsElement<Integer> {

	protected int value;
	
	public AbstractIntegerStatisticsElement(int value) {
		super(StatisticType.INTEGER);
		this.value = value;
	}

	@Override
	public Integer getValue() {
		return value;
	}
	
	@Override
	public void setValue(Integer value) {
		this.value = value;
	}
	
}
