package se.de.hu_berlin.informatik.utils.statistics;

import se.de.hu_berlin.informatik.utils.statistics.StatisticsAPI.StatisticType;

public class BooleanStatisticsElement extends AbstractStatisticsElement<Boolean> {

	private boolean value;
	private boolean prefTrue = true;
	private boolean prefNew = false;
	private boolean prefOld = false;
	
	public BooleanStatisticsElement(boolean value, StatisticsOptions... options) {
		super(StatisticType.BOOLEAN);
		this.value = value;
		for (StatisticsOptions option : options) {
			if (option == StatisticsOptions.PREF_NEW) {
				prefNew = true;
				prefOld = false;
			} else if (option == StatisticsOptions.PREF_OLD) {
				prefNew = false;
				prefOld = true;
			} else if (option == StatisticsOptions.PREF_TRUE) {
				prefTrue = true;
			} else if (option == StatisticsOptions.PREF_FALSE) {
				prefTrue = false;
			}
		}
	}
	
	@Override
	public Boolean getValue() {
		return value;
	}

	@Override
	public void mergeWith(StatisticsElement<?> element) {
		if (this.getType() == element.getType()) {
			//if preferring old values, use the existing value and do nothing
			if (!prefOld) {
				if (prefNew) {
					//use the latest element
					value = (Boolean)element.getValue();
				} else if (prefTrue) {
					//if the other element is true, then set this one to true, too
					if ((Boolean)element.getValue()) {
						value = true;
					}
				} else {
					//if the other element is false, then set this one to false, too
					if (!(Boolean)element.getValue()) {
						value = false;
					}
				}
			}
		}
	}
	
	@Override
	public String getValueAsString() {
		return String.valueOf(value);
	}
	
}
