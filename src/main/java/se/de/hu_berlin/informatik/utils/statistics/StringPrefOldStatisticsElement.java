package se.de.hu_berlin.informatik.utils.statistics;

public class StringPrefOldStatisticsElement extends AbstractStringStatisticsElement {

	public StringPrefOldStatisticsElement(String value) {
		super(value);
	}

	@Override
	public void mergeWith(StatisticsElement<?> element) {
		if (this.getType() == element.getType()) {
			//if this element is not null, then use it; otherwise use the other one
			if (this.getValue() == null) {
				this.setValue((String)element.getValue());
			}
		}
	}
	
}
