package se.de.hu_berlin.informatik.utils.statistics;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class StatisticsContainer<T extends Enum<T> & Labeled> {
	
	private Map<T, StatisticsElementList> statisticsElements;
	private int statisticsCounter;
	
	public StatisticsContainer() {
		this.statisticsElements = new HashMap<>();
		this.statisticsCounter = 0;
	}

	/**
	 * @return
	 * the number of added statistics
	 */
	public int getNumberOfStatistics() {
		return statisticsCounter;
	}
	
	public StatisticsElementList getStatisticsElementList(String identifier) {
		return statisticsElements.get(identifier);
	}
	
	/**
	 * @return
	 * a map with lists of added statistics elements
	 */
	public Map<T, StatisticsElementList> getStatisticsElements() {
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
						k -> new StringStatisticsElementList())
						.addElement(element.getValue());
				break;
			case BOOLEAN:
				temp = statisticsElements.computeIfAbsent(element.getKey(), 
						k -> new BooleanStatisticsElementList())
						.addElement(element.getValue());
				break;
			case DOUBLE:
				temp = statisticsElements.computeIfAbsent(element.getKey(), 
						k -> new DoubleStatisticsElementList())
						.addElement(element.getValue());
				break;
			case INTEGER:
				temp = statisticsElements.computeIfAbsent(element.getKey(), 
						k -> new IntegerStatisticsElementList())
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

	private String getStatistics(Entry<T, StatisticsElementList> statisticsEntry) {
		switch (statisticsEntry.getValue().getType()) {
		case STRING:
			StringStatisticsElementList stringList = (StringStatisticsElementList) statisticsEntry.getValue();
			StringBuilder builder = new StringBuilder();
			for (String element : stringList.getElements()) {
				if (element != null) {
					builder.append(element);
					builder.append(System.lineSeparator());
				}
			}
			return stringStatistics(statisticsEntry.getKey().getLabel(), builder.toString());
		case BOOLEAN:
			BooleanStatisticsElementList booleanList = (BooleanStatisticsElementList) statisticsEntry.getValue();
			int trueCount = 0;
			int falseCount = 0;
			for (boolean element : booleanList.getElements()) {
				if (element) {
					++trueCount;
				} else {
					++falseCount;
				}
			}
			return booleanStatistics(statisticsEntry.getKey().getLabel(), trueCount, falseCount);
		case DOUBLE:
			DoubleStatisticsElementList doubleList = (DoubleStatisticsElementList) statisticsEntry.getValue();
			int doubleCount = 0;
			double doubleSum = 0;
			double doubleMin = Double.POSITIVE_INFINITY;
			double doubleMax = Double.NEGATIVE_INFINITY;
			for (double element : doubleList.getElements()) {
				++doubleCount;
				doubleSum += element;
				doubleMin = doubleMin > element ? element : doubleMin;
				doubleMax = doubleMax < element ? element : doubleMax;
			}
			return doubleStatistics(statisticsEntry.getKey().getLabel(), doubleCount, doubleSum, doubleMin, doubleMax);
		case INTEGER:
			IntegerStatisticsElementList integerList = (IntegerStatisticsElementList) statisticsEntry.getValue();
			int integerCount = 0;
			for (int element : integerList.getElements()) {
				integerCount += element;
			}
			return integerStatistics(statisticsEntry.getKey().getLabel(), integerCount);
		default:
			break;
		}
		return "";
	}
	
	private String stringStatistics(String identifier, String string) {
		return identifier + " -> <start>" + System.lineSeparator() + string + identifier + " -> <end>";
	}

	private String doubleStatistics(String identifier, int doubleCount, double doubleSum, double doubleMin, double doubleMax) {
		return identifier + " -> count: " + doubleCount + ", min: " + doubleMin + ", max: " + doubleMax + ", mean: " + (doubleSum/(double)doubleCount);
	}

	private String integerStatistics(String identifier, int count) {
		return identifier + " -> count: " + count;
	}

	private String booleanStatistics(String identifier, int trueCount, int falseCount) {
		return identifier + " -> true: " + trueCount + ", false: " + falseCount + ", unknown: " + (statisticsCounter - trueCount - falseCount);
	}

	public String printStatistics() {
		StringBuilder builder = new StringBuilder();
		for (Entry<T, StatisticsElementList> statisticsEntry : statisticsElements.entrySet()) {
			builder.append(getStatistics(statisticsEntry));
			builder.append(System.lineSeparator());
		}
		return builder.toString();
	}

}
