package se.de.hu_berlin.informatik.utils.statistics;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Statistics<T extends Enum<T> & Labeled> {
	
	public static enum StatisticType {
		BOOLEAN,
		INTEGER,
		DOUBLE,
		STRING
	}
	
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

	private boolean addElement(T identifier, StatisticsElement<?> element) {
		if (elements.containsKey(identifier)) {
			return false;
		} else {
			elements.put(identifier, element);
			return true;
		}
	}
	
	public boolean addCountingElementPrefBigger(T identifier, int value) {
		return addElement(identifier, new IntegerPrefBiggerStatisticsElement(value));
	}
	
	public boolean addCountingElementPrefSmaller(T identifier, int value) {
		return addElement(identifier, new IntegerPrefSmallerStatisticsElement(value));
	}
	
	public boolean addBooleanElementPrefTrue(T identifier, boolean value) {
		return addElement(identifier, new BooleanPrefTrueStatisticsElement(value));
	}
	
	public boolean addBooleanElementPrefFalse(T identifier, boolean value) {
		return addElement(identifier, new BooleanPrefFalseStatisticsElement(value));
	}
	
	public boolean addStringElementPrefNew(T identifier, String value) {
		return addElement(identifier, new StringPrefNewStatisticsElement(value));
	}
	
	public boolean addStringElementPrefOld(T identifier, String value) {
		return addElement(identifier, new StringPrefOldStatisticsElement(value));
	}
	
	public boolean addDoubleValueElementPrefBigger(T identifier, double value) {
		return addElement(identifier, new DoublePrefBiggerStatisticsElement(value));
	}
	
	public boolean addDoubleValueElementPrefSmaller(T identifier, double value) {
		return addElement(identifier, new DoublePrefSmallerStatisticsElement(value));
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
