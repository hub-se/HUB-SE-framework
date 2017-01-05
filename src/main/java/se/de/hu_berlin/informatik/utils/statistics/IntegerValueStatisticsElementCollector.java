package se.de.hu_berlin.informatik.utils.statistics;

import java.util.ArrayList;
import java.util.List;

import se.de.hu_berlin.informatik.utils.statistics.StatisticsAPI.StatisticType;

public class IntegerValueStatisticsElementCollector extends AbstractStatisticsElementCollector {
	
	private List<Integer> elements;
	
	public IntegerValueStatisticsElementCollector() {
		super(StatisticType.INTEGER_VALUE);
		this.elements = new ArrayList<>();
	}
	
	@Override
	public boolean addElement(StatisticsElement<?> element) {
		if (element.getType() != this.getType()) {
			return false;
		}
		elements.add((Integer)element.getValue());
		return true;
	}
	
	public List<Integer> getElements() {
		return elements;
	}
	
}
