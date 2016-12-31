package se.de.hu_berlin.informatik.utils.statistics;

public class DoublePrefSmallerStatisticsElement extends AbstractDoubleStatisticsElement {

	public DoublePrefSmallerStatisticsElement(double value) {
		super(value);
	}

	@Override
	public void mergeWith(StatisticsElement<?> element) {
		if (this.getType() == element.getType()) {
			double elementValue = (double)element.getValue();
			//if the other element is smaller, use it
			if (elementValue < value) {
				this.setValue(elementValue);
			}
		}
	}
	
}
