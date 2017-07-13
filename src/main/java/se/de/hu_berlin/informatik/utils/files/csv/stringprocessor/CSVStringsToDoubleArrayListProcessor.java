/**
 * 
 */
package se.de.hu_berlin.informatik.utils.files.csv.stringprocessor;

import java.util.ArrayList;
import java.util.List;

import se.de.hu_berlin.informatik.utils.files.csv.CSVUtils;
import se.de.hu_berlin.informatik.utils.files.processors.FileLineProcessor.StringProcessor;

/**
 * Takes Strings in CSV format and generates a list of Double arrays.
 * 
 * @author Simon Heiden
 */
public class CSVStringsToDoubleArrayListProcessor implements StringProcessor<List<Double[]>> {

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
	@Override
	public boolean process(String line) {
		String[] temp = line.split(CSVUtils.CSV_DELIMITER_STRING, -1);
		Double[] array = new Double[temp.length];
		for (int i = 0; i < temp.length; ++i) {
			array[i] = temp[i].equals("null") ? null : Double.valueOf(temp[i]);
		}
		return lines.add(array);
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.modules.stringprocessor.IStringProcessor#getResult()
	 */
	@Override
	public List<Double[]> getFileResult() {
		List<Double[]> temp = lines;
		lines = new ArrayList<>();
		return temp;
	}

}
