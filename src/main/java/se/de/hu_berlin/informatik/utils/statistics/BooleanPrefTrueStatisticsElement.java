package se.de.hu_berlin.informatik.utils.statistics;

public class BooleanPrefTrueStatisticsElement extends AbstractBooleanStatisticsElement {

	public BooleanPrefTrueStatisticsElement(boolean value) {
		super(value);
	}

	@Override
	public void mergeWith(StatisticsElement<?> element) {
		if (this.getType() == element.getType()) {
			//if the other element is true, then set this one to true, too
			if ((Boolean)element.getValue()) {
				this.setValue(true);
			}
		}
	}
	
}
