/**
 * 
 */
package se.de.hu_berlin.informatik.utils.files.processors.stringprocessor;

import java.util.ArrayList;
import java.util.List;

import se.de.hu_berlin.informatik.utils.files.processors.FileLineProcessor.StringProcessor;

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

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.modules.stringprocessor.IStringProcessor#getResult()
	 */
	public List<String> getFileResult() {
		List<String> temp = lines;
		lines = new ArrayList<>();
		return temp;
	}	
	

}
