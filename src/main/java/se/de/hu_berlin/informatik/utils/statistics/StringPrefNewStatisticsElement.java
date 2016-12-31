package se.de.hu_berlin.informatik.utils.statistics;

public class StringPrefNewStatisticsElement extends AbstractStringStatisticsElement {

	public StringPrefNewStatisticsElement(String value) {
		super(value);
	}

	@Override
	public void mergeWith(StatisticsElement<?> element) {
		if (this.getType() == element.getType()) {
			String elementValue = (String)element.getValue();
			//if the other element is not null, then use it
			if (elementValue != null) {
				this.setValue(elementValue);
			}
		}
	}
	
}
