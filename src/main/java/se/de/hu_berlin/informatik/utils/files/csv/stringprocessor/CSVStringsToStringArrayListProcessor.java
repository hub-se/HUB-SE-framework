/**
 * 
 */
package se.de.hu_berlin.informatik.utils.files.csv.stringprocessor;

import java.util.ArrayList;
import java.util.List;

import se.de.hu_berlin.informatik.utils.files.csv.CSVUtils;
import se.de.hu_berlin.informatik.utils.files.processors.FileLineProcessor.StringProcessor;

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
	@Override
	public boolean process(String line) {
		String[] temp = line.split(CSVUtils.CSV_DELIMITER_STRING, -1);
		return lines.add(temp);
	}

	/**
	 * @return 
	 * null
	 */
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.modules.stringprocessor.IStringProcessor#getResult()
	 */
	@Override
	public List<String[]> getFileResult() {
		List<String[]> temp = lines;
		lines = new ArrayList<>();
		return temp;
	}

}
