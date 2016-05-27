/**
 * 
 */
package se.de.hu_berlin.informatik.utils.fileoperations;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;
import se.de.hu_berlin.informatik.utils.miscellaneous.OutputPathGenerator;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AModule;

/**
 * A file writer module for multiple files that gets for example a list 
 * of list of {@link String}s and writes their contents to specified
 * output paths. Output file names
 * may also be generated automatically with an included instance of
 * {@link OutputPathGenerator}. The given input is returned as it is
 * to the output in the end in case it has to be further processed.
 * 
 * @author Simon Heiden
 * 
 * @see OutputPathGenerator
 */
public class MultiFileWriterModule<A extends Iterable<? extends Iterable<? extends CharSequence>> > extends AModule<A, A> {

	private boolean generateOutputPaths = false;
	private Path[] paths = null;
	private String extension;
	boolean overwrite = false;
	
	OutputPathGenerator generator;
	
	/**
	 * Creates a new {@link MultiFileWriterModule} with the given parameters.
	 * @param outputdir
	 * is either a directory or an output file path
	 * @param overwrite
	 * determines if files and directories should be overwritten
	 * @param extension
	 * is the extension of the automatically generated output paths
	 */
	public MultiFileWriterModule(Path outputdir, boolean overwrite, String extension) {
		super(true);
		this.extension = extension;
		this.generator = new OutputPathGenerator(outputdir, overwrite);
		this.generateOutputPaths = true;
	}
	
	/**
	 * Creates a new {@link MultiFileWriterModule} with the given parameters.
	 * @param overwrite
	 * determines if files and directories should be overwritten
	 * @param paths
	 * is a sequence of output paths that are used by the file writer
	 */
	public MultiFileWriterModule(boolean overwrite, Path... paths) {
		super(true);
		this.paths = paths;
		this.overwrite = overwrite;
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public A processItem(A items) {
		Iterator<? extends Iterable<? extends CharSequence>>  iterator = items.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			Path outputPath = null;
			if (generateOutputPaths) {
				outputPath = generator.getNewOutputPath(extension);
			} else {
				try {
					outputPath = paths[i];
					++i;
					if (outputPath.toFile().isDirectory()) {
						Misc.err(this, "Path \"%s\" is a directory and should be a file.", outputPath.toString());
						break;
					}
					if (!overwrite && outputPath.toFile().exists()) {
						Misc.err(this, "File \"%s\" exists.", outputPath.toString());
						break;
					}
					outputPath.getParent().toFile().mkdirs();
				} catch (IndexOutOfBoundsException e) {
					Misc.abort(this, "No output path for file %d given.", i+1);
				}
			}
			try {
				Files.write(outputPath, iterator.next(), StandardCharsets.UTF_8);
			} catch (IOException e) {
				Misc.abort(this, e, "Cannot write file \"" + outputPath.toString() + "\".");
			}
		}
		return items;
	}

}
