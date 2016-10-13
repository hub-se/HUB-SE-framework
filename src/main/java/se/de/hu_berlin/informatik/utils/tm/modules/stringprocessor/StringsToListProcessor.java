/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.modules.stringprocessor;

import java.util.ArrayList;
import java.util.List;
import se.de.hu_berlin.informatik.utils.tm.modules.stringprocessor.StringProcessor;

/**
 * Takes Strings and generates a list of all the processed Strings.
 * 
 * @author Simon Heiden
 */
public class StringsToListProcessor implements StringProcessor<List<String>> {

	private List<String> lines;
	
	/**
	 * Creates a new {@link StringsToListProcessor} object.
	 */
	public StringsToListProcessor() {
		lines = new ArrayList<>();
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.modules.stringprocessor.IStringProcessor#process(java.lang.String)
	 */
	public boolean process(String line) {
		return lines.add(line);
	}

	/**
	 * @return 
	 * null
	 */
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.modules.stringprocessor.IStringProcessor#getResult()
	 */
	public List<String> getResult() {
		return null;
	}

	@Override
	public List<String> getResultFromCollectedItems() {
		return lines;
	}
	
	

}
