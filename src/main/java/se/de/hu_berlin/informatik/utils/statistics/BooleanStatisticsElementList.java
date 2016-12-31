package se.de.hu_berlin.informatik.utils.statistics;

import java.util.ArrayList;
import java.util.List;

import se.de.hu_berlin.informatik.utils.statistics.Statistics.StatisticType;

public class BooleanStatisticsElementList extends AbstractStatisticsElementList {
	
	private List<Boolean> elements;
	
	public BooleanStatisticsElementList() {
		super(StatisticType.BOOLEAN);
		this.elements = new ArrayList<>();
	}
	
	@Override
	public boolean addElement(StatisticsElement<?> element) {
		if (element.getType() != this.getType()) {
			return false;
		}
		elements.add((boolean)element.getValue());
		return true;
	}

	public List<Boolean> getElements() {
		return elements;
	}
	
}
