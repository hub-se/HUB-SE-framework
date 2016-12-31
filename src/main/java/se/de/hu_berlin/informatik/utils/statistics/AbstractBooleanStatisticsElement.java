package se.de.hu_berlin.informatik.utils.statistics;

import se.de.hu_berlin.informatik.utils.statistics.Statistics.StatisticType;

public abstract class AbstractBooleanStatisticsElement extends AbstractStatisticsElement<Boolean> {

	private boolean value;
	
	public AbstractBooleanStatisticsElement(boolean value) {
		super(StatisticType.BOOLEAN);
		this.value = value;
	}

	@Override
	public Boolean getValue() {
		return value;
	}
	
	@Override
	public void setValue(Boolean value) {
		this.value = value;
	}
	
}
