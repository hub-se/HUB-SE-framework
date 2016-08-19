package se.de.hu_berlin.informatik.utils.fileoperations.csv;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import se.de.hu_berlin.informatik.utils.fileoperations.FileLineProcessorModule;

/**
 * Contains utility methods to cope with reading and writing CSV files.
 *
 * @author Simon Heiden
 */
public final class CSVUtils {

    /**
     * used CSV delimiter
     */
    public static final String CSV_DELIMITER = "\t";
    
    /**
     * Reads a CSV data file and parses its contents into a list of Double arrays.
     * @param csvFile
     * the path to the CSV file
     * @return
     * a list of Double arrays
     */
    public static List<Double[]> readCSVFileToListOfDoubleArrays(Path csvFile) {
    	return new FileLineProcessorModule<List<Double[]>>(new CSVStringsToDoubleArrayListProcessor(), false)
    			.submit(csvFile)
    			.getResultFromCollectedItems();
    }
    
    /**
     * Reads a CSV data file and parses its contents into a list of Integer arrays.
     * @param csvFile
     * the path to the CSV file
     * @return
     * a list of Integer arrays
     */
    public static List<Integer[]> readCSVFileToToListOfIntegerArrays(Path csvFile) {
    	return new FileLineProcessorModule<List<Integer[]>>(new CSVStringsToIntegerArrayListProcessor(), false)
    			.submit(csvFile)
    			.getResultFromCollectedItems();
    }
    
    /**
     * Reads a CSV data file and parses its contents into a list of String arrays.
     * @param csvFile
     * the path to the CSV file
     * @return
     * a list of String arrays
     */
    public static List<String[]> readCSVFileToToListOfStringArrays(Path csvFile) {
    	return new FileLineProcessorModule<List<String[]>>(new CSVStringsToStringArrayListProcessor(), false)
    			.submit(csvFile)
    			.getResultFromCollectedItems();
    }
    
    /**
     * Turns an integer array into a CSV line.
     * @param dataArray 
     * an array containing the data elements
     * @param columnCount
     * the number of columns
     * @return 
     * the combined CSV string to write to a file
     */
    public static List<String> toCsv(final int[] dataArray, int columnCount) {
        final StringBuilder line = new StringBuilder();
        Assert.assertTrue(dataArray.length % columnCount == 0);
        
        List<String> lines = new ArrayList<>();
        
        for (int i = 0; i < dataArray.length; ++i) {
            line.append(dataArray[i]);
            if ((i+1) % columnCount == 0) {
            	//add the line to the list and begin a new line
            	lines.add(line.toString());
            	line.setLength(0);
            } else {
            	//put delimiter between the values
            	line.append(CSV_DELIMITER);
            }
        }
        return lines;
    }
    
    /**
     * Turns a byte array into a CSV line.
     * @param dataArray 
     * an array containing the data elements
     * @param columnCount
     * the number of columns
     * @return 
     * the combined CSV string to write to a file
     */
    public static List<String> toCsv(final byte[] dataArray, int columnCount) {
        final StringBuilder line = new StringBuilder();
        Assert.assertTrue(dataArray.length % columnCount == 0);
        
        List<String> lines = new ArrayList<>();
        
        for (int i = 0; i < dataArray.length; ++i) {
            line.append(dataArray[i]);
            if ((i+1) % columnCount == 0) {
            	//add the line to the list and begin a new line
            	lines.add(line.toString());
            	line.setLength(0);
            } else {
            	//put delimiter between the values
            	line.append(CSV_DELIMITER);
            }
        }
        return lines;
    }

}
