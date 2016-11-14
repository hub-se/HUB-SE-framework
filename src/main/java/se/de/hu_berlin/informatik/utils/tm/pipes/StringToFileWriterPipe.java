/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.pipes;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.miscellaneous.OutputPathGenerator;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipe;

/**
 * A file writer pipe that gets a sequence of Strings
 * and writes them to a specified output file.
 * The given input is returned as it is
 * to the output in the end in case it has to be further processed.
 * 
 * @author Simon Heiden
 * 
 * @see OutputPathGenerator
 */
public class StringToFileWriterPipe extends AbstractPipe<String, String> {

	private Path outputPath;
	
	private BufferedWriter writer = null;
	
	/**
	 * Creates a new {@link StringToFileWriterPipe} with the given parameters.
	 * @param outputPath
	 * is either a directory or an output file path
	 * @param overwrite
	 * determines if files and directories should be overwritten
	 */
	public StringToFileWriterPipe(Path outputPath, boolean overwrite) {
		super(true);
		this.outputPath = outputPath;
		if (outputPath.toFile().isDirectory()) {
			Log.abort(this, "Path \"%s\" is a directory and should be a file.", outputPath.toString());
		}
		if (!overwrite && outputPath.toFile().exists()) {
			Log.abort(this, "File \"%s\" exists.", outputPath.toString());
		}
		outputPath.getParent().toFile().mkdirs();
		
		try {
			writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(outputPath), StandardCharsets.UTF_8.newEncoder()));
		} catch (IOException e) {
			Log.abort(this, e, "Cannot open '%s' for writing.", outputPath);
		}
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.miscellaneous.ITransmitter#processItem(java.lang.Object)
	 */
	public String processItem(String item) {
		try {
			writer.append(item.toString());
			writer.newLine();
		} catch(IOException e) {
			Log.err(this, e, "Cannot write line to '%s'.", outputPath);
		}
		return item;
	}
	
	@Override
	protected void finalize() throws Throwable {
		if (writer != null) {
			writer.close();
		}
		super.finalize();
	}

	@Override
	public boolean finalShutdown() {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				Log.abort(this, e, "Cannot close output stream.");
			}
		}
		return super.finalShutdown();
	}

	
	
}
