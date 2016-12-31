package se.de.hu_berlin.informatik.utils.statistics;

import se.de.hu_berlin.informatik.utils.statistics.Statistics.StatisticType;

public abstract class AbstractDoubleStatisticsElement extends AbstractStatisticsElement<Double> {

	protected double value;
	
	public AbstractDoubleStatisticsElement(double value) {
		super(StatisticType.DOUBLE);
		this.value = value;
	}

	@Override
	public Double getValue() {
		return value;
	}
	
	@Override
	public void setValue(Double value) {
		this.value = value;
	}
	
}
