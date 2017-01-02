package se.de.hu_berlin.informatik.utils.statistics;

import se.de.hu_berlin.informatik.utils.statistics.StatisticsAPI.StatisticType;

public class StringStatisticsElement extends AbstractStatisticsElement<String> {

	private String value = null;
	private boolean prefNew = true;
	
	public StringStatisticsElement(String value, StatisticsOptions... options) {
		super(StatisticType.STRING);
		this.value = value;
		for (StatisticsOptions option : options) {
			if (option == StatisticsOptions.PREF_NEW) {
				prefNew = true;
			} else if (option == StatisticsOptions.PREF_OLD) {
				prefNew = false;
			}
		}
	}
	
	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void mergeWith(StatisticsElement<?> element) {
		if (this.getType() == element.getType()) {
			String elementValue = (String)element.getValue();
			if (prefNew) {
				//if the other element is not null, then use it
				if (elementValue != null) {
					value = elementValue;
				}
			} else {
				//if this element is not null, then use it; otherwise use the other one
				if (this.getValue() == null) {
					value = elementValue;
				}
			}
		}
	}
	
}
