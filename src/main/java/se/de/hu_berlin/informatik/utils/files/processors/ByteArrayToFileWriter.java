/**
 * 
 */
package se.de.hu_berlin.informatik.utils.files.processors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.miscellaneous.OutputPathGenerator;
import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;

/**
 * A file writer module that gets a byte array 
 * and writes its contents to a specified output file. Output file names
 * may also be generated automatically with an included instance of
 * {@link OutputPathGenerator}. The given input is returned as it is
 * to the output in the end in case it has to be further processed.
 * 
 * @author Simon Heiden
 * 
 * @see OutputPathGenerator
 */
public class ByteArrayToFileWriter extends AbstractProcessor<byte[], byte[]> {

	private Path outputPath;
	private Path outputdir;
	private boolean generateOutputPaths = false;
	private String extension;
	
	OutputPathGenerator generator;
	
	/**
	 * Creates a new {@link ByteArrayToFileWriter} with the given parameters.
	 * @param outputPath
	 * is either a directory or an output file path
	 * @param overwrite
	 * determines if files and directories should be overwritten
	 * @param generateOutputPaths
	 * determines if output paths should be generated automatically
	 * @param extension
	 * is the extension of the automatically generated output paths
	 */
	public ByteArrayToFileWriter(Path outputPath, boolean overwrite, boolean generateOutputPaths, String extension) {
		super();
		this.outputPath = outputPath;
		this.generateOutputPaths = generateOutputPaths;
		this.extension = extension;
		if (generateOutputPaths) {
			this.outputdir = outputPath;
			this.generator = new OutputPathGenerator(outputdir, overwrite);
		} else {
			if (outputPath.toFile().isDirectory()) {
				Log.abort(this, "Path \"%s\" is a directory and should be a file.", outputPath.toString());
			}
			if (!overwrite && outputPath.toFile().exists()) {
				Log.abort(this, "File \"%s\" exists.", outputPath.toString());
			}
		}
	}
	
	/**
	 * Creates a new {@link ByteArrayToFileWriter} with the given parameters.
	 * @param outputPath
	 * is either a directory or an output file path
	 * @param overwrite
	 * determines if files and directories should be overwritten
	 */
	public ByteArrayToFileWriter(Path outputPath, boolean overwrite) {
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
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.miscellaneous.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public byte[] processItem(byte[] item) {
		if (generateOutputPaths) {
			outputPath = generator.getNewOutputPath(extension);
		}
		try {
			Files.write(outputPath, item);
		} catch (IOException e) {
			Log.abort(this, e, "Cannot write file \"" + outputPath.toString() + "\".");
		}
		return item;
	}

}
