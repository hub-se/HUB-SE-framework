package se.de.hu_berlin.informatik.utils.statistics;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import se.de.hu_berlin.informatik.utils.fileoperations.ListToFileWriterModule;
import se.de.hu_berlin.informatik.utils.fileoperations.csv.CSVUtils;
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

	private boolean addStatisticsElementInstance(T identifier, StatisticsElement<?> elementToAdd) {
		StatisticsElement<?> element = elements.get(identifier);
		if (element == null) {
			elements.put(identifier, elementToAdd);
		} else {
			element.mergeWith(elementToAdd);
		}
//		if (elements.containsKey(identifier)) {
//			return false;
//		} else {
//			elements.put(identifier, element);
//			return true;
//		}
		return true;
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

	public void saveToCSV(Path output) {
		List<String[]> list = new ArrayList<>();
		for (Entry<T, StatisticsElement<?>> statisticsEntry : elements.entrySet()) {
			String[] array = { statisticsEntry.getKey().name(), statisticsEntry.getValue().getValueAsString() };
			list.add(array);
		}
		new ListToFileWriterModule<List<String>>(output, true).submit(CSVUtils.toCsv(list));
	}
	
	public static <T extends Enum<T> & StatisticsAPI> Statistics<T> loadAndMergeFromCSV(Class<T> clazz, Path input) {
		Statistics<T> statistics = new Statistics<T>();
		List<String[]> list = CSVUtils.readCSVFileToListOfStringArrays(input, false);
		for (String[] array : list) {
			if (array.length == 2) {
				T enumKey = Enum.valueOf(clazz, array[0]);
				switch (enumKey.getType()) {
				case BOOLEAN:
					statistics.addStatisticsElement(enumKey, Boolean.valueOf(array[1]));
					break;
				case COUNT:
					statistics.addStatisticsElement(enumKey, Integer.valueOf(array[1]));
					break;
				case DOUBLE_VALUE:
					statistics.addStatisticsElement(enumKey, Double.valueOf(array[1]));
					break;
				case INTEGER_VALUE:
					statistics.addStatisticsElement(enumKey, Integer.valueOf(array[1]));
					break;
				case STRING:
					statistics.addStatisticsElement(enumKey, array[1]);
					break;
				default:
					Log.err(Statistics.class, "No strategy for type '%s' available.", enumKey.getType());
					break;
				}
			} else {
				Log.err(Statistics.class, "CSV file '%s' has the wrong format.", input);
				return null;
			}
		}
		return statistics;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Entry<T, StatisticsElement<?>> statisticsEntry : elements.entrySet()) {
			builder.append(statisticsEntry.getKey().getLabel() + " -> " + statisticsEntry.getValue().getValueAsString());
			builder.append(System.lineSeparator());
		}
		return builder.toString();
	}
	
}
