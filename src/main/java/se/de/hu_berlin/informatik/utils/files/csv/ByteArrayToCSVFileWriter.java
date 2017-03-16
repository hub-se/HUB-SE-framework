/**
 * 
 */
package se.de.hu_berlin.informatik.utils.files.csv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.miscellaneous.OutputPathGenerator;
import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;

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
public class ByteArrayToCSVFileWriter extends AbstractProcessor<byte[], byte[]> {

	/**
     * used CSV delimiter
     */
    public static final String CSV_DELIMITER = ";";
    
	private Path outputPath;
	private int columnCount;
	
	/**
	 * Creates a new {@link ByteArrayToCSVFileWriter} with the given parameters.
	 * @param outputPath
	 * is either a directory or an output file path
	 * @param columnCount
	 * the number of columns that the csv data file should contain
	 * @param overwrite
	 * determines if files and directories should be overwritten
	 */
	public ByteArrayToCSVFileWriter(Path outputPath, int columnCount, boolean overwrite) {
		super();
		this.outputPath = outputPath;
		if (outputPath.toFile().isDirectory()) {
			Log.abort(this, "Path \"%s\" is a directory and should be a file.", outputPath.toString());
		}
		if (!overwrite && outputPath.toFile().exists()) {
			Log.abort(this, "File \"%s\" exists.", outputPath.toString());
		}
		if (outputPath.getParent() != null) {
			outputPath.getParent().toFile().mkdirs();
		}
		this.columnCount = columnCount;
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.miscellaneous.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public byte[] processItem(byte[] item) {
		try {
			Files.write(outputPath, CSVUtils.toCsv(item, columnCount));
		} catch (IOException e) {
			Log.abort(this, e, "Cannot write file \"" + outputPath.toString() + "\".");
		}
		return item;
	}
	
}
