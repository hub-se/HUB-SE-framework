package se.de.hu_berlin.informatik.utils.statistics;

public class IntegerPrefSmallerStatisticsElement extends AbstractIntegerStatisticsElement {

	public IntegerPrefSmallerStatisticsElement(int value) {
		super(value);
	}

	@Override
	public void mergeWith(StatisticsElement<?> element) {
		if (this.getType() == element.getType()) {
			int elementValue = (int)element.getValue();
			//if the other element is smaller, use it
			if (elementValue < value) {
				this.setValue(elementValue);
			}
		}
	}
	
}
