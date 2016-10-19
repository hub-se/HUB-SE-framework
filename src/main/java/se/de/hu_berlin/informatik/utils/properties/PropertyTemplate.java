package se.de.hu_berlin.informatik.utils.properties;

public interface PropertyTemplate {

	public String getPropertyIdentifier();
	
	public String getPlaceHolder();
	
	public String[] getHelpfulDescription();
	
	public void setPropertyValue(String value);
	
	public String getValue();
}
