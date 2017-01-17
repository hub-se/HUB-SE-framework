package se.de.hu_berlin.informatik.utils.statistics;

import se.de.hu_berlin.informatik.utils.statistics.StatisticsAPI.StatisticType;

public class DoubleValueStatisticsElement extends AbstractStatisticsElement<Double> {

	private double value = 0;
	private boolean prefBigger = true;
	private boolean prefNew = false;
	private boolean prefOld = false;
	
	public DoubleValueStatisticsElement(double value, StatisticsOptions... options) {
		super(StatisticType.DOUBLE_VALUE);
		this.value = value;
		for (StatisticsOptions option : options) {
			if (option == StatisticsOptions.PREF_NEW) {
				prefNew = true;
				prefOld = false;
			} else if (option == StatisticsOptions.PREF_OLD) {
				prefNew = false;
				prefOld = true;
			} else if (option == StatisticsOptions.PREF_BIGGER) {
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
			//if preferring old values, use the existing value and do nothing
			if (!prefOld) {
				double elementValue = (Double)element.getValue();
				if (prefNew) {
					//use the latest element
					value = elementValue;
				} else if (prefBigger) {
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
	
	@Override
	public String getValueAsString() {
		return String.valueOf(value);
	}

	@Override
	public boolean getValueAsBoolean() {
		return false;
	}

	@Override
	public int getValueAsInteger() {
		return (int) value;
	}

	@Override
	public double getValueAsDouble() {
		return value;
	}
	
}
