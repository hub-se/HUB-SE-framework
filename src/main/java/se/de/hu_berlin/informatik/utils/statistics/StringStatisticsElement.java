package se.de.hu_berlin.informatik.utils.statistics;

import se.de.hu_berlin.informatik.utils.statistics.StatisticsAPI.StatisticType;

public class StringStatisticsElement extends AbstractStatisticsElement<String> {

	private String value = null;
	private boolean prefNew = true;
	private boolean prefOld = false;
	private boolean concat = false;
	
	public StringStatisticsElement(String value, StatisticsOptions... options) {
		super(StatisticType.STRING);
		this.value = value;
		for (StatisticsOptions option : options) {
			if (option == StatisticsOptions.PREF_NEW) {
				prefNew = true;
				prefOld = false;
			} else if (option == StatisticsOptions.PREF_OLD) {
				prefNew = false;
				prefOld = true;
			} else if (option == StatisticsOptions.CONCAT) {
				concat = true;
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
			//concatenation takes precedence
			if (concat) {
				//if the other element is not null, then concat it to
				//an existing, non-null element
				if (elementValue != null) {
					if (value == null) {
						value = elementValue;	
					} else {
						value += System.lineSeparator() + elementValue;
					}
				}
			} else if (prefOld) {
				//if this element is not null, then use it; 
				//otherwise use the other one
				if (value == null) {
					value = elementValue;
				}
			}else if (prefNew) {
				//if the other element is not null, then use it
				if (elementValue != null) {
					value = elementValue;
				}
			}  else { //prefOld
				//if this element is not null, then use it; 
				//otherwise use the other one
				if (value == null) {
					value = elementValue;
				}
			}
		}
	}

	@Override
	public String getValueAsString() {
		return value;
	}

	@Override
	public boolean getValueAsBoolean() {
		return Boolean.parseBoolean(value);
	}

	@Override
	public int getValueAsInteger() throws NumberFormatException {
		return Integer.valueOf(value);
	}

	@Override
	public double getValueAsDouble() throws NumberFormatException {
		return Double.valueOf(value);
	}
	
}
