package se.de.hu_berlin.informatik.utils.optionparser;

public interface OptionCarrier {

	public OptionParser getOptions();
	
	public OptionCarrier setOptions(OptionParser options);
	
	public boolean hasOptions();
	
}
