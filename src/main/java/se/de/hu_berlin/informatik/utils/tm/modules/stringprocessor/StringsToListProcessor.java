/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.modules.stringprocessor;

import java.util.ArrayList;
import java.util.List;
import se.de.hu_berlin.informatik.utils.tm.modules.stringprocessor.IStringProcessor;

/**
 * Takes Strings and generates a list of all the processed Strings.
 * 
 * @author Simon Heiden
 */
public class StringsToListProcessor implements IStringProcessor {

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
	 * a list of all processed Strings
	 */
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.modules.stringprocessor.IStringProcessor#getResult()
	 */
	public Object getResult() {
		return lines;
	}

}
