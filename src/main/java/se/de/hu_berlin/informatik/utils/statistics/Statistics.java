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
		if (identifier.getType() == StatisticType.INTEGER) {
			return addStatisticsElementInstance(identifier, new IntegerStatisticsElement(value, identifier.getOptions()));
		} else {
			Log.err(this, "Values to add to statistics '%s' should not be of type INTEGER.", identifier.name());
			return false;
		}
	}
	
	public boolean addStatisticsElement(T identifier, double value) {
		if (identifier.getType() == StatisticType.DOUBLE) {
			return addStatisticsElementInstance(identifier, new DoubleStatisticsElement(value, identifier.getOptions()));
		} else {
			Log.err(this, "Values to add to statistics '%s' should not be of type DOUBLE.", identifier.name());
			return false;
		}
	}
	
	public boolean addStatisticsElement(T identifier, boolean value) {
		if (identifier.getType() == StatisticType.BOOLEAN) {
			return addStatisticsElementInstance(identifier, new BooleanStatisticsElement(value, identifier.getOptions()));
		} else {
			Log.err(this, "Values to add to statistics '%s' should not be of type BOOLEAN.", identifier.name());
			return false;
		}
	}
	
	public boolean addStatisticsElement(T identifier, String value) {
		if (identifier.getType() == StatisticType.STRING) {
			return addStatisticsElementInstance(identifier, new StringStatisticsElement(value, identifier.getOptions()));
		} else {
			Log.err(this, "Values to add to statistics '%s' should not be of type STRING.", identifier.name());
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
