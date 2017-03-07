/**
 * 
 */
package se.de.hu_berlin.informatik.utils.fileoperations;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import se.de.hu_berlin.informatik.utils.tm.AbstractProcessor;
import se.de.hu_berlin.informatik.utils.tm.modules.stringprocessor.StringProcessor;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;

/**
 * Module that reads a submitted file and processes each line with the given
 * instance of a class that implements the interface {@link StringProcessor}.
 * In the end, a result object is returned to the output.
 * 
 * <br><br> Returns null in case of an error.
 * 
 * @author Simon Heiden
 * @param A
 * the type of the return objects of the used {@link StringProcessor}
 */
public class FileLineProcessorModule<A> extends AbstractProcessor<Path, A> {

	public static Charset[] charsets = { 
			StandardCharsets.UTF_8, StandardCharsets.ISO_8859_1, 
			StandardCharsets.US_ASCII, StandardCharsets.UTF_16,
			StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE};
	
	private StringProcessor<A> processor;
	private boolean abortOnError = false;
	
	private int skip = 0;
	
	/**
	 * Creates a new {@link FileLineProcessorModule} object with the given parameters.
	 * Will continue execution if a line can't be processed or produces an error.
	 * @param processor
	 * {@link StringProcessor} object that takes a String and processes it 
	 * or null
	 */
	public FileLineProcessorModule(StringProcessor<A> processor) {
		this(processor, false);
	}
	
	public FileLineProcessorModule<A> skipFirstLines(int count) {
		skip = count;
		return this;
	}
	
	/**
	 * Creates a new {@link FileLineProcessorModule} object with the given parameters.
	 * @param processor
	 * {@link StringProcessor} object that takes a String and processes it
	 * @param abortOnError
	 * whether the execution should be aborted when encountering an error
	 */
	public FileLineProcessorModule(StringProcessor<A> processor, boolean abortOnError) {
		super();
		this.processor = processor;
		this.abortOnError = abortOnError;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public A processItem(Path input) {
		//try opening the file with different charsets
		for (Charset charset : charsets) {
			//try opening the file
			try (BufferedReader reader = Files.newBufferedReader(input , charset)) {
				String line;
				while ((line = reader.readLine()) != null) {
					if (skip > 0) {
						--skip;
						continue;
					}
					if (!processor.process(line)) {
						if (abortOnError) {
							Log.abort(this, "Processing line \"%s\" with %s was not successful.", line, processor.getClass().getSimpleName());
						} else {
							Log.warn(this, "Processing line \"%s\" with %s was not successful.", line, processor.getClass().getSimpleName());
						}
					}
				}

				return processor.getResult();

			} catch (IOException x) {
				//try next charset
			}
		}
		if (abortOnError) {
			Log.abort(this, "Not able to open/read file %s.", input.toString());
		} else {
			Log.warn(this, "Not able to open/read file %s.", input.toString());
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#getResultFromCollectedItems()
	 */
	@Override
	public A getResultFromCollectedItems() {
		return processor.getResultFromCollectedItems();
	}
	
}
