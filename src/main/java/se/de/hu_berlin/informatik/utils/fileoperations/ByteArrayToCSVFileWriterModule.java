/**
 * 
 */
package se.de.hu_berlin.informatik.utils.fileoperations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;
import se.de.hu_berlin.informatik.utils.miscellaneous.OutputPathGenerator;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AModule;

/**
 * A file writer module that gets a byte array 
 * and writes its contents to a specified output file as a CSV data
 * file with the bytes as data elements. The given input is returned 
 * as it is to the output in the end in case it has to be further processed.
 * 
 * @author Simon Heiden
 * 
 * @see OutputPathGenerator
 */
public class ByteArrayToCSVFileWriterModule extends AModule<byte[], byte[]> {

	/**
     * used CSV delimiter
     */
    public static final String CSV_DELIMITER = ";";
    
	private Path outputPath;
	private int columnCount;
	
	/**
	 * Creates a new {@link ByteArrayToCSVFileWriterModule} with the given parameters.
	 * @param outputPath
	 * is either a directory or an output file path
	 * @param columnCount
	 * the number of columns that the csv data file should contain
	 * @param overwrite
	 * determines if files and directories should be overwritten
	 */
	public ByteArrayToCSVFileWriterModule(Path outputPath, int columnCount, boolean overwrite) {
		super(true);
		this.outputPath = outputPath;
		if (outputPath.toFile().isDirectory()) {
			Misc.abort(this, "Path \"%s\" is a directory and should be a file.", outputPath.toString());
		}
		if (!overwrite && outputPath.toFile().exists()) {
			Misc.abort(this, "File \"%s\" exists.", outputPath.toString());
		}
		if (outputPath.getParent() != null) {
			outputPath.getParent().toFile().mkdirs();
		}
		this.columnCount = columnCount;
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.miscellaneous.ITransmitter#processItem(java.lang.Object)
	 */
	public byte[] processItem(byte[] item) {
		try {
			Files.write(outputPath, toCsv(item, columnCount));
		} catch (IOException e) {
			Misc.abort(this, e, "Cannot write file \"" + outputPath.toString() + "\".");
		}
		return item;
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
        final StringBuffer line = new StringBuffer();
        Assert.assertTrue(dataArray.length % columnCount == 0);
        
        List<String> lines = new ArrayList<>();
        
        for (int i = 0; i < dataArray.length; ++i) {
            line.append(dataArray[i]);
            if ((i+1) % columnCount == 0) {
            	lines.add(line.toString());
            	line.setLength(0);
            } else {
            	line.append(CSV_DELIMITER);
            }
        }
        return lines;
    }

}
