/**
 * 
 */
package se.de.hu_berlin.informatik.utils.fileoperations.csv;

import java.util.ArrayList;
import java.util.List;
import se.de.hu_berlin.informatik.utils.tm.modules.stringprocessor.IStringProcessor;

/**
 * Takes Strings in CSV format and generates a list of String arrays.
 * 
 * @author Simon Heiden
 */
public class CSVStringsToMirroredStringArrayListProcessor implements IStringProcessor<List<String[]>> {

	private List<String[]> lines;
	
	/**
	 * Creates a new {@link CSVStringsToMirroredStringArrayListProcessor} object.
	 */
	public CSVStringsToMirroredStringArrayListProcessor() {
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
		return mirror(lines);
	}
	
	private static List<String[]> mirror(List<String[]> lines) {
		if (lines.size() == 0) {
    		return new ArrayList<>(0);
    	}
    	//assert same length of arrays
    	int arrayLength = lines.get(0).length;
    	for (String[] element : lines) {
            assert element.length == arrayLength;
        }
    	
    	List<String[]> mirrored = new ArrayList<>();
        
        for (int i = 0; i < arrayLength; ++i) {
        	mirrored.add(toStringArray(lines, i));
        }
		
		return mirrored;
	}
	
	private static String[] toStringArray(final List<String[]> stringArrayList, int columnIndex) {
        String[] temp = new String[stringArrayList.size()];
        for (int i = 0; i < stringArrayList.size(); ++i) {
            temp[i] = stringArrayList.get(i)[columnIndex];
        }
        return temp;
    }

}
