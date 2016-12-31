package se.de.hu_berlin.informatik.utils.statistics;

import java.util.ArrayList;
import java.util.List;

import se.de.hu_berlin.informatik.utils.statistics.Statistics.StatisticType;

public class IntegerStatisticsElementList extends AbstractStatisticsElementList {
	
	private List<Integer> elements;
	
	public IntegerStatisticsElementList() {
		super(StatisticType.INTEGER);
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
