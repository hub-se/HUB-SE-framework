package se.de.hu_berlin.informatik.utils.statistics;

import java.util.ArrayList;
import java.util.List;

import se.de.hu_berlin.informatik.utils.statistics.StatisticsAPI.StatisticType;

public class StringStatisticsElementCollector extends AbstractStatisticsElementCollector {
	
	private List<String> elements;
	
	public StringStatisticsElementCollector() {
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
