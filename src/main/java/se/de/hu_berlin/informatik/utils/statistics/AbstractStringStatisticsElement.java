package se.de.hu_berlin.informatik.utils.statistics;

import se.de.hu_berlin.informatik.utils.statistics.Statistics.StatisticType;

public abstract class AbstractStringStatisticsElement extends AbstractStatisticsElement<String> {

	private String value;
	
	public AbstractStringStatisticsElement(String value) {
		super(StatisticType.STRING);
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}
	
	@Override
	public void setValue(String value) {
		this.value = value;
	}
	
}
