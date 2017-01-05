package se.de.hu_berlin.informatik.utils.statistics;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.statistics.StatisticsAPI.StatisticType;

public class Statistics<T extends Enum<T> & StatisticsAPI> {
	
	private Map<T, StatisticsElement<?>> elements;
	
	public Statistics() {
		this.elements = new HashMap<>();
	}
	
	public int getNumberOfStatisticsElements() {
		return elements.size();
	}
	
	public StatisticsElement<?> getElement(T identifier) {
		return elements.get(identifier);
	}

	public Map<T, StatisticsElement<?>> getElements() {
		return elements;
	}

	private boolean addStatisticsElementInstance(T identifier, StatisticsElement<?> element) {
		if (elements.containsKey(identifier)) {
			return false;
		} else {
			elements.put(identifier, element);
			return true;
		}
	}
	
	public boolean addStatisticsElement(T identifier, int value) {
		if (identifier.getType() == StatisticType.COUNT) {
			return addStatisticsElementInstance(identifier, new CountingStatisticsElement(value, identifier.getOptions()));
		} else if (identifier.getType() == StatisticType.INTEGER_VALUE) {
			return addStatisticsElementInstance(identifier, new IntegerValueStatisticsElement(value, identifier.getOptions()));
		} else if (identifier.getType() == StatisticType.DOUBLE_VALUE) {
			return addStatisticsElementInstance(identifier, new DoubleValueStatisticsElement((double)value, identifier.getOptions()));
		} else {
			Log.err(this, "Can not add INTEGER to statistics '%s'.", identifier.name());
			return false;
		}
	}
	
	public boolean addStatisticsElement(T identifier, double value) {
		if (identifier.getType() == StatisticType.COUNT) {
			return addStatisticsElementInstance(identifier, new CountingStatisticsElement((int)value, identifier.getOptions()));
		} else if (identifier.getType() == StatisticType.INTEGER_VALUE) {
			return addStatisticsElementInstance(identifier, new IntegerValueStatisticsElement((int)value, identifier.getOptions()));
		} else if (identifier.getType() == StatisticType.DOUBLE_VALUE) {
			return addStatisticsElementInstance(identifier, new DoubleValueStatisticsElement(value, identifier.getOptions()));
		} else {
			Log.err(this, "Can not add DOUBLE to statistics '%s'.", identifier.name());
			return false;
		}
	}
	
	public boolean addStatisticsElement(T identifier, boolean value) {
		if (identifier.getType() == StatisticType.BOOLEAN) {
			return addStatisticsElementInstance(identifier, new BooleanStatisticsElement(value, identifier.getOptions()));
		} else {
			Log.err(this, "Can not add BOOLEAN to statistics '%s'.", identifier.name());
			return false;
		}
	}
	
	public boolean addStatisticsElement(T identifier, String value) {
		if (identifier.getType() == StatisticType.STRING) {
			return addStatisticsElementInstance(identifier, new StringStatisticsElement(value, identifier.getOptions()));
		} else {
			Log.err(this, "Can not add String to statistics '%s'.", identifier.name());
			return false;
		}
	}

	public Statistics<T> mergeWith(Statistics<T> statistics) {
		for (Entry<T, StatisticsElement<?>> entry : statistics.getElements().entrySet()) {
			StatisticsElement<?> element = elements.get(entry.getKey());
			if (element == null) {
				elements.put(entry.getKey(), entry.getValue());
			} else {
				element.mergeWith(entry.getValue());
			}
		}
		
		return this;
	}

}
