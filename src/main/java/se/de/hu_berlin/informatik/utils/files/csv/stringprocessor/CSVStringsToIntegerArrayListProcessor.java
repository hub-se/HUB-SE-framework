/**
 * 
 */
package se.de.hu_berlin.informatik.utils.files.csv.stringprocessor;

import java.util.ArrayList;
import java.util.List;

import se.de.hu_berlin.informatik.utils.files.csv.CSVUtils;
import se.de.hu_berlin.informatik.utils.files.processors.FileLineProcessor.StringProcessor;

/**
 * Takes Strings in CSV format and generates a list of Integer arrays.
 * 
 * @author Simon Heiden
 */
public class CSVStringsToIntegerArrayListProcessor implements StringProcessor<List<Integer[]>> {

	private List<Integer[]> lines;
	
	/**
	 * Creates a new {@link CSVStringsToIntegerArrayListProcessor} object.
	 */
	public CSVStringsToIntegerArrayListProcessor() {
		lines = new ArrayList<>();
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.modules.stringprocessor.IStringProcessor#process(java.lang.String)
	 */
	public boolean process(String line) {
		String[] temp = line.split(CSVUtils.CSV_DELIMITER, -1);
		Integer[] array = new Integer[temp.length];
		for (int i = 0; i < temp.length; ++i) {
			array[i] = temp[i].equals("null") ? null : Integer.valueOf(temp[i]);
		}
		return lines.add(array);
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.modules.stringprocessor.IStringProcessor#getResult()
	 */
	public List<Integer[]> getFileResult() {
		List<Integer[]> temp = lines;
		lines = new ArrayList<>();
		return temp;
	}

}
