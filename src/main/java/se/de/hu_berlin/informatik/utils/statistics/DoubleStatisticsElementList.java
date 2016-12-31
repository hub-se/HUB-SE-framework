package se.de.hu_berlin.informatik.utils.statistics;

import java.util.ArrayList;
import java.util.List;

import se.de.hu_berlin.informatik.utils.statistics.Statistics.StatisticType;

public class DoubleStatisticsElementList extends AbstractStatisticsElementList {
	
	private List<Double> elements;
	
	public DoubleStatisticsElementList() {
		super(StatisticType.DOUBLE);
		this.elements = new ArrayList<>();
	}
	
	@Override
	public boolean addElement(StatisticsElement<?> element) {
		if (element.getType() != this.getType()) {
			return false;
		}
		elements.add((double)element.getValue());
		return true;
	}
	
	public List<Double> getElements() {
		return elements;
	}
	
}
