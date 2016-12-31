package se.de.hu_berlin.informatik.utils.statistics;

import java.util.ArrayList;
import java.util.List;

import se.de.hu_berlin.informatik.utils.statistics.Statistics.StatisticType;

public class StringStatisticsElementList extends AbstractStatisticsElementList {
	
	private List<String> elements;
	
	public StringStatisticsElementList() {
		super(StatisticType.STRING);
		this.elements = new ArrayList<>();
	}
	
	@Override
	public boolean addElement(StatisticsElement<?> element) {
		if (element.getType() != this.getType()) {
			return false;
		}
		elements.add((String)element.getValue());
		return true;
	}

	public List<String> getElements() {
		return elements;
	}
	
}
