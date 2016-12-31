package se.de.hu_berlin.informatik.utils.statistics;

public class IntegerPrefBiggerStatisticsElement extends AbstractIntegerStatisticsElement {

	public IntegerPrefBiggerStatisticsElement(int value) {
		super(value);
	}

	@Override
	public void mergeWith(StatisticsElement<?> element) {
		if (this.getType() == element.getType()) {
			int elementValue = (int)element.getValue();
			//if the other element is bigger, use it
			if (elementValue > value) {
				this.setValue(elementValue);
			}
		}
	}
	
}
