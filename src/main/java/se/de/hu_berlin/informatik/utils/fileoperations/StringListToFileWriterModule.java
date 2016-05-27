/**
 * 
 */
package se.de.hu_berlin.informatik.utils.fileoperations;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;
import se.de.hu_berlin.informatik.utils.miscellaneous.OutputPathGenerator;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AModule;

/**
 * A file writer module that gets for example a list of {@link String}s 
 * and writes its contents to a specified output file. Output file names
 * may also be generated automatically with an included instance of
 * {@link OutputPathGenerator}. The given input is returned as it is
 * to the output in the end in case it has to be further processed.
 * 
 * @author Simon Heiden
 * 
 * @param A
 * the type of iterable char sequence 
 * 
 * @see OutputPathGenerator
 */
public class StringListToFileWriterModule<A extends Iterable<? extends CharSequence> > extends AModule<A, A> {

	private Path outputPath;
	private Path outputdir;
	private boolean generateOutputPaths = false;
	private String extension;
	
	OutputPathGenerator generator;
	
	/**
	 * Creates a new {@link StringListToFileWriterModule} with the given parameters.
	 * @param outputPath
	 * is either a directory or an output file path
	 * @param overwrite
	 * determines if files and directories should be overwritten
	 * @param generateOutputPaths
	 * determines if output paths should be generated automatically
	 * @param extension
	 * is the extension of the automatically generated output paths
	 */
	public StringListToFileWriterModule(Path outputPath, boolean overwrite, boolean generateOutputPaths, String extension) {
		super(true);
		this.outputPath = outputPath;
		this.generateOutputPaths = generateOutputPaths;
		this.extension = extension;
		if (generateOutputPaths) {
			this.outputdir = outputPath;
			this.generator = new OutputPathGenerator(outputdir, overwrite);
		} else {
			if (outputPath.toFile().isDirectory()) {
				Misc.abort(this, "Path \"%s\" is a directory and should be a file.", outputPath.toString());
			}
			if (!overwrite && outputPath.toFile().exists()) {
				Misc.abort(this, "File \"%s\" exists.", outputPath.toString());
			}
		}
	}
	
	/**
	 * Creates a new {@link StringListToFileWriterModule} with the given parameters.
	 * @param outputPath
	 * is either a directory or an output file path
	 * @param overwrite
	 * determines if files and directories should be overwritten
	 */
	public StringListToFileWriterModule(Path outputPath, boolean overwrite) {
		super(true);
		this.outputPath = outputPath;
		if (outputPath.toFile().isDirectory()) {
			Misc.abort(this, "Path \"%s\" is a directory and should be a file.", outputPath.toString());
		}
		if (!overwrite && outputPath.toFile().exists()) {
			Misc.abort(this, "File \"%s\" exists.", outputPath.toString());
		}
		outputPath.getParent().toFile().mkdirs();
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.miscellaneous.ITransmitter#processItem(java.lang.Object)
	 */
	public A processItem(A item) {
		if (generateOutputPaths) {
			outputPath = generator.getNewOutputPath(extension);
		}
		try {
			Files.write(outputPath, item, StandardCharsets.UTF_8);
		} catch (IOException e) {
			Misc.abort(this, e, "Cannot write file \"" + outputPath.toString() + "\".");
		}
		return item;
	}

}
