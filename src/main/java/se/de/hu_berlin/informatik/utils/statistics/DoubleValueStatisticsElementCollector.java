package se.de.hu_berlin.informatik.utils.statistics;

import java.util.ArrayList;
import java.util.List;

import se.de.hu_berlin.informatik.utils.statistics.StatisticsAPI.StatisticType;

public class DoubleValueStatisticsElementCollector extends AbstractStatisticsElementCollector {
	
	private List<Double> elements;
	
	public DoubleValueStatisticsElementCollector() {
		super(StatisticType.DOUBLE_VALUE);
		this.elements = new ArrayList<>();
	}
	
	@Override
	public boolean addElement(StatisticsElement<?> element) {
		if (element.getType() != this.getType()) {
			return false;
		}
		elements.add((Double)element.getValue());
		return true;
	}
	
	public List<Double> getElements() {
		return elements;
	}
	
}
