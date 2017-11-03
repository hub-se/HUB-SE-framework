/**
 * 
 */
package se.de.hu_berlin.informatik.utils.files.processors;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Objects;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.miscellaneous.OutputPathGenerator;
import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;

/**
 * A file writer module that gets for example a List object
 * and writes its contents to a specified output file. Output file names
 * may also be generated automatically with an included instance of
 * {@link OutputPathGenerator}. The given input is returned as it is
 * to the output in the end in case it has to be further processed.
 * 
 * @author Simon Heiden
 * 
 * @param A
 * the type of iterable object 
 * 
 * @see OutputPathGenerator
 */
public class ListToFileWriter<A extends Iterable<?> > extends AbstractProcessor<A, A> {

	private Path outputPath;
	private Path outputdir;
	private boolean generateOutputPaths = false;
	private String extension;
	
	OutputPathGenerator generator;
	
	/**
	 * Creates a new {@link ListToFileWriter} with the given parameters.
	 * @param outputPath
	 * is either a directory or an output file path
	 * @param overwrite
	 * determines if files and directories should be overwritten
	 * @param generateOutputPaths
	 * determines if output paths should be generated automatically
	 * @param extension
	 * is the extension of the automatically generated output paths
	 */
	public ListToFileWriter(Path outputPath, boolean overwrite, boolean generateOutputPaths, String extension) {
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
	 * Creates a new {@link ListToFileWriter} with the given parameters.
	 * @param outputPath
	 * is either a directory or an output file path
	 * @param overwrite
	 * determines if files and directories should be overwritten
	 */
	public ListToFileWriter(Path outputPath, boolean overwrite) {
		super();
		this.outputPath = outputPath;
		if (outputPath.toFile().isDirectory()) {
			Log.abort(this, "Path \"%s\" is a directory and should be a file.", outputPath.toString());
		}
		if (!overwrite && outputPath.toFile().exists()) {
			Log.abort(this, "File \"%s\" exists.", outputPath.toString());
		}
		outputPath.getParent().toFile().mkdirs();
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.miscellaneous.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public A processItem(A item) {
		if (generateOutputPaths) {
			outputPath = generator.getNewOutputPath(extension);
		}
		try {
			write(outputPath, item, StandardCharsets.UTF_8);
		} catch (IOException e) {
//			Log.err(this, e, "Cannot write file \"" + outputPath.toString() + "\".");
			try {
				write(outputPath, item, StandardCharsets.ISO_8859_1);
			} catch (IOException e1) {
//				Log.err(this, e1, "Cannot write file \"" + outputPath.toString() + "\".");
				try {
					write(outputPath, item, StandardCharsets.UTF_16);
				} catch (IOException e2) {
					Log.err(this, e2, "Cannot write file \"" + outputPath.toString() + "\".");
				}
			}
		}
		return item;
	}
	
	private Path write(Path path, Iterable<?> lines,
			Charset cs, OpenOption... options) throws IOException {
		// ensure lines is not null before opening file
		Objects.requireNonNull(lines);
		CharsetEncoder encoder = cs.newEncoder();
		OutputStream out = Files.newOutputStream(path, options);
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, encoder))) {
			for (Object line: lines) {
				writer.append(line.toString());
				writer.newLine();
			}
		}
		return path;
	}

}
