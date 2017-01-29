package se.de.hu_berlin.informatik.utils.optionparser;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;

public interface OptionCarrier {

	public OptionParser getOptions();
	
	public OptionCarrier setOptions(OptionParser options);
	
	public boolean hasOptions();
	
	/**
	 * Aborts if no options object is set.
	 */
	default public void requireOptions() {
		if (!hasOptions()) {
			Log.abort(this, "No options object is set. Options required to continue operation!");
		}
	}
	
}
