/**
 * 
 */
package se.de.hu_berlin.informatik.utils.fileoperations.csv;

import java.util.ArrayList;
import java.util.List;
import se.de.hu_berlin.informatik.utils.tm.modules.stringprocessor.IStringProcessor;

/**
 * Takes Strings in CSV format and generates a list of Integer arrays.
 * 
 * @author Simon Heiden
 */
public class CSVStringsToIntegerArrayListProcessor implements IStringProcessor<List<Integer[]>> {

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
		String[] temp = line.split(CSVUtils.CSV_DELIMITER);
		Integer[] array = new Integer[temp.length];
		for (int i = 0; i < temp.length; ++i) {
			array[i] = Integer.valueOf(temp[i]);
		}
		return lines.add(array);
	}

	/**
	 * @return 
	 * null
	 */
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.modules.stringprocessor.IStringProcessor#getResult()
	 */
	public List<Integer[]> getResult() {
		return null;
	}

	@Override
	public List<Integer[]> getResultFromCollectedItems() {
		return lines;
	}
	
	

}
