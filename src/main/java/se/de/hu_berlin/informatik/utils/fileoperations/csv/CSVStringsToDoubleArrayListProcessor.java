/**
 * 
 */
package se.de.hu_berlin.informatik.utils.fileoperations.csv;

import java.util.ArrayList;
import java.util.List;
import se.de.hu_berlin.informatik.utils.tm.modules.stringprocessor.IStringProcessor;

/**
 * Takes Strings in CSV format and generates a list of Double arrays.
 * 
 * @author Simon Heiden
 */
public class CSVStringsToDoubleArrayListProcessor implements IStringProcessor<List<Double[]>> {

	private List<Double[]> lines;
	
	/**
	 * Creates a new {@link CSVStringsToDoubleArrayListProcessor} object.
	 */
	public CSVStringsToDoubleArrayListProcessor() {
		lines = new ArrayList<>();
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.modules.stringprocessor.IStringProcessor#process(java.lang.String)
	 */
	public boolean process(String line) {
		String[] temp = line.split(CSVUtils.CSV_DELIMITER);
		Double[] array = new Double[temp.length];
		for (int i = 0; i < temp.length; ++i) {
			array[i] = Double.valueOf(temp[i]);
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
	public List<Double[]> getResult() {
		return null;
	}

	@Override
	public List<Double[]> getResultFromCollectedItems() {
		return lines;
	}
	
	

}
