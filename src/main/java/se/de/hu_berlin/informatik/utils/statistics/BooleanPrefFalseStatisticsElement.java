package se.de.hu_berlin.informatik.utils.statistics;

public class BooleanPrefFalseStatisticsElement extends AbstractBooleanStatisticsElement {

	public BooleanPrefFalseStatisticsElement(boolean value) {
		super(value);
	}

	@Override
	public void mergeWith(StatisticsElement<?> element) {
		if (this.getType() == element.getType()) {
			//if the other element is false, then set this one to false, too
			if (!(Boolean)element.getValue()) {
				this.setValue(false);
			}
		}
	}
	
}
