package se.de.hu_berlin.informatik.utils.statistics;

public class DoublePrefBiggerStatisticsElement extends AbstractDoubleStatisticsElement {

	public DoublePrefBiggerStatisticsElement(double value) {
		super(value);
	}

	@Override
	public void mergeWith(StatisticsElement<?> element) {
		if (this.getType() == element.getType()) {
			double elementValue = (double)element.getValue();
			//if the other element is bigger, use it
			if (elementValue > value) {
				this.setValue(elementValue);
			}
		}
	}
	
}
