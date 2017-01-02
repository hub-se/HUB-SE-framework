package se.de.hu_berlin.informatik.utils.statistics;

import se.de.hu_berlin.informatik.utils.statistics.StatisticsAPI.StatisticType;

public class DoubleStatisticsElement extends AbstractStatisticsElement<Double> {

	private double value = 0;
	private boolean prefBigger = true;
	
	public DoubleStatisticsElement(double value, StatisticsOptions... options) {
		super(StatisticType.DOUBLE);
		this.value = value;
		for (StatisticsOptions option : options) {
			if (option == StatisticsOptions.PREF_BIGGER) {
				prefBigger = true;
			} else if (option == StatisticsOptions.PREF_SMALLER) {
				prefBigger = false;
			}
		}
	}
	
	@Override
	public Double getValue() {
		return value;
	}

	@Override
	public void mergeWith(StatisticsElement<?> element) {
		if (this.getType() == element.getType()) {
			double elementValue = (Double)element.getValue();
			if (prefBigger) {
				//if the other element is bigger, use it
				if (elementValue > value) {
					value = elementValue;
				}
			} else {
				//if the other element is smaller, use it
				if (elementValue < value) {
					value = elementValue;
				}
			}
		}
	}
	
}
