package se.de.hu_berlin.informatik.utils.statistics;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class StatisticsCollector<T extends Enum<T> & StatisticsAPI> {
	
	final private Map<T, StatisticsElementCollector> statisticsElements;
	private int statisticsCounter;
	final private Class<T> statisticsClazz;
	
	public StatisticsCollector(Class<T> statisticsClazz) {
		this.statisticsElements = new HashMap<>();
		this.statisticsCounter = 0;
		this.statisticsClazz = statisticsClazz;
	}

	/**
	 * @return
	 * the number of added statistics
	 */
	public int getNumberOfStatistics() {
		return statisticsCounter;
	}
	
	public StatisticsElementCollector getStatisticsElementList(T identifier) {
		return statisticsElements.get(identifier);
	}
	
	/**
	 * @return
	 * a map with lists of added statistics elements
	 */
	public Map<T, StatisticsElementCollector> getStatisticsElements() {
		return statisticsElements;
	}

	/**
	 * Adds a statistics object to the container
	 * @param statistics
	 * the statistics to add
	 * @return
	 * true if adding the statistics was successful; false otherwise
	 */
	public boolean addStatistics(Statistics<T> statistics) {
		boolean result = true;
		for (Entry<T, StatisticsElement<?>> element : statistics.getElements().entrySet()) {
			boolean temp = true;
			switch(element.getValue().getType()) {
			case STRING:
				temp = statisticsElements.computeIfAbsent(element.getKey(), 
						k -> new StringStatisticsElementCollector())
						.addElement(element.getValue());
				break;
			case BOOLEAN:
				temp = statisticsElements.computeIfAbsent(element.getKey(), 
						k -> new BooleanStatisticsElementCollector())
						.addElement(element.getValue());
				break;
			case DOUBLE_VALUE:
				temp = statisticsElements.computeIfAbsent(element.getKey(), 
						k -> new DoubleValueStatisticsElementCollector())
						.addElement(element.getValue());
				break;
			case INTEGER_VALUE:
				temp = statisticsElements.computeIfAbsent(element.getKey(), 
						k -> new IntegerValueStatisticsElementCollector())
						.addElement(element.getValue());
				break;
			case COUNT:
				temp = statisticsElements.computeIfAbsent(element.getKey(), 
						k -> new CountingStatisticsElementCollector())
						.addElement(element.getValue());
				break;
			default:
				temp = false;
				break;
			}
			result &= temp;
		}
		
		++statisticsCounter;
		return result;
	}
	
	public boolean addStatisticsElement(T identifier, int value) {
		Statistics<T> statistics = new Statistics<T>();
		statistics.addStatisticsElement(identifier, value);
		return this.addStatistics(statistics);
	}
	
	public boolean addStatisticsElement(T identifier, double value) {
		Statistics<T> statistics = new Statistics<T>();
		statistics.addStatisticsElement(identifier, value);
		return this.addStatistics(statistics);
	}
	
	public boolean addStatisticsElement(T identifier, String value) {
		Statistics<T> statistics = new Statistics<T>();
		statistics.addStatisticsElement(identifier, value);
		return this.addStatistics(statistics);
	}
	
	public boolean addStatisticsElement(T identifier, boolean value) {
		Statistics<T> statistics = new Statistics<T>();
		statistics.addStatisticsElement(identifier, value);
		return this.addStatistics(statistics);
	}

	private String getStatistics(T statisticsEntry, StatisticsElementCollector list) {
		switch (statisticsEntry.getType()) {
		case STRING:
			StringBuilder builder = new StringBuilder();
			if (list != null) {
				StringStatisticsElementCollector stringList = (StringStatisticsElementCollector) list;
				for (String element : stringList.getElements()) {
					if (element != null) {
						builder.append("\t  " + element);
						builder.append(System.lineSeparator());
					}
				}
			}
			return stringStatistics(statisticsEntry.getLabel(), builder.toString());
		case BOOLEAN:
			int trueCount = 0;
			int falseCount = 0;
			if (list != null) {
				BooleanStatisticsElementCollector booleanList = (BooleanStatisticsElementCollector) list;
				for (boolean element : booleanList.getElements()) {
					if (element) {
						++trueCount;
					} else {
						++falseCount;
					}
				}
			}
			return booleanStatistics(statisticsEntry.getLabel(), trueCount, falseCount);
		case DOUBLE_VALUE:
			int doubleCount = 0;
			double doubleSum = 0;
			double doubleMin = Double.POSITIVE_INFINITY;
			double doubleMax = Double.NEGATIVE_INFINITY;
			if (list != null) {
				DoubleValueStatisticsElementCollector doubleList = (DoubleValueStatisticsElementCollector) list;
				for (double element : doubleList.getElements()) {
					++doubleCount;
					doubleSum += element;
					doubleMin = doubleMin > element ? element : doubleMin;
					doubleMax = doubleMax < element ? element : doubleMax;
				}
			}
			return doubleStatistics(statisticsEntry.getLabel(), doubleCount, doubleSum, doubleMin, doubleMax);
		case INTEGER_VALUE:
			int integerCount = 0;
			long integerSum = 0;
			int integerMin = Integer.MAX_VALUE;
			int integerMax = Integer.MIN_VALUE;
			if (list != null) {
				IntegerValueStatisticsElementCollector integerList = (IntegerValueStatisticsElementCollector) list;
				for (int element : integerList.getElements()) {
					++integerCount;
					integerSum += element;
					integerMin = integerMin > element ? element : integerMin;
					integerMax = integerMax < element ? element : integerMax;
				}
			}
			return integerStatistics(statisticsEntry.getLabel(), integerCount, integerSum, integerMin, integerMax);
		case COUNT:
			long count = 0;
			if (list != null) {
				count = ((CountingStatisticsElementCollector) list).getElementCount();
			}
			return countStatistics(statisticsEntry.getLabel(), count);
		default:
			break;
		}
		return "";
	}
	
	private String stringStatistics(String identifier, String string) {
		return identifier + " -> <start>" + System.lineSeparator() + string + "\t" + identifier + " -> <end>";
	}

	private String doubleStatistics(String identifier, int doubleCount, double doubleSum, double doubleMin, double doubleMax) {
		return identifier + " -> min: " + doubleMin + ", max: " + doubleMax + ", mean: " + (doubleSum/(double)doubleCount);
	}
	
	private String integerStatistics(String identifier, int integerCount, long integerSum, int integerMin, int integerMax) {
		return identifier + " -> min: " + integerMin + ", max: " + integerMax + ", mean: " + ((double)integerSum/(double)integerCount);
	}

	private String countStatistics(String identifier, long count) {
		return identifier + " -> count: " + count;
	}

	private String booleanStatistics(String identifier, int trueCount, int falseCount) {
		String result = identifier + " -> true: " + trueCount + ", false: " + falseCount;
		int unknown = statisticsCounter - trueCount - falseCount;
		if (unknown == 0) {
			return result;
		} else {
			return result + ", unknown: " + unknown;
		}
	}

	public String printStatistics() {
		return printStatistics(EnumSet.allOf(statisticsClazz));
	}
	
	public String printStatistics(EnumSet<T> statisticsEntries) {
		StringBuilder builder = new StringBuilder();
		builder.append(System.lineSeparator());
		for (T statisticsEntry : statisticsEntries) {
			printStatisticsForSingleEntry(builder, statisticsEntry);
		}
		return builder.toString();
	}
	
	public String printStatistics(Collection<T> statisticsEntries) {
		StringBuilder builder = new StringBuilder();
		for (T statisticsEntry : statisticsEntries) {
			printStatisticsForSingleEntry(builder, statisticsEntry);
		}
		return builder.toString();
	}
	
	public String printStatistics(@SuppressWarnings("unchecked") T... statisticsEntries) {
		StringBuilder builder = new StringBuilder();
		for (T statisticsEntry : statisticsEntries) {
			printStatisticsForSingleEntry(builder, statisticsEntry);
		}
		return builder.toString();
	}

	private void printStatisticsForSingleEntry(StringBuilder builder, T statisticsEntry) {
		StatisticsElementCollector list = statisticsElements.get(statisticsEntry);
		builder.append("\t" + getStatistics(statisticsEntry, list));
		builder.append(System.lineSeparator());
	}

}
