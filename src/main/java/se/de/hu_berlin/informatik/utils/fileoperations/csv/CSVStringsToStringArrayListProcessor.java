/**
 * 
 */
package se.de.hu_berlin.informatik.utils.fileoperations.csv;

import java.util.ArrayList;
import java.util.List;
import se.de.hu_berlin.informatik.utils.tm.modules.stringprocessor.StringProcessor;

/**
 * Takes Strings in CSV format and generates a list of String arrays.
 * 
 * @author Simon Heiden
 */
public class CSVStringsToStringArrayListProcessor implements StringProcessor<List<String[]>> {

	private List<String[]> lines;
	
	/**
	 * Creates a new {@link CSVStringsToStringArrayListProcessor} object.
	 */
	public CSVStringsToStringArrayListProcessor() {
		lines = new ArrayList<>();
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.modules.stringprocessor.IStringProcessor#process(java.lang.String)
	 */
	public boolean process(String line) {
		String[] temp = line.split(CSVUtils.CSV_DELIMITER);
		return lines.add(temp);
	}

	/**
	 * @return 
	 * null
	 */
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.modules.stringprocessor.IStringProcessor#getResult()
	 */
	public List<String[]> getResult() {
		return null;
	}

	@Override
	public List<String[]> getResultFromCollectedItems() {
		return lines;
	}
	
	

}
