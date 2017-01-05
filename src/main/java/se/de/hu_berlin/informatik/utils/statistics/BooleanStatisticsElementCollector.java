package se.de.hu_berlin.informatik.utils.statistics;

import java.util.ArrayList;
import java.util.List;

import se.de.hu_berlin.informatik.utils.statistics.StatisticsAPI.StatisticType;

public class BooleanStatisticsElementCollector extends AbstractStatisticsElementCollector {
	
	private List<Boolean> elements;
	
	public BooleanStatisticsElementCollector() {
		super(StatisticType.BOOLEAN);
		this.elements = new ArrayList<>();
	}
	
	@Override
	public boolean addElement(StatisticsElement<?> element) {
		if (element.getType() != this.getType()) {
			return false;
		}
		elements.add((Boolean)element.getValue());
		return true;
	}

	public List<Boolean> getElements() {
		return elements;
	}
	
}
