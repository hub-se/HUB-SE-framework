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

import se.de.hu_berlin.informatik.utils.tm.moduleframework.AModule;
import se.de.hu_berlin.informatik.utils.tm.modules.stringprocessor.IStringProcessor;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;

/**
 * Module that reads a submitted file and processes each line with the given
 * instance of a class that implements the interface {@link IStringProcessor}.
 * In the end, a result object is returned to the output.
 * 
 * <br><br> Returns null in case of an error.
 * 
 * @author Simon Heiden
 * @param A
 * the type of the return objects of the used {@link IStringProcessor}
 */
public class FileLineProcessorModule<A> extends AModule<Path, A> {

	public static Charset[] charsets = { 
			StandardCharsets.UTF_8, StandardCharsets.ISO_8859_1, 
			StandardCharsets.US_ASCII, StandardCharsets.UTF_16,
			StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE};
	
	private IStringProcessor<A> processor;
	private boolean abortOnError = false;
	
	/**
	 * Creates a new {@link FileLineProcessorModule} object with the given parameters.
	 * Will continue execution if a line can't be processed or produces an error.
	 * @param processor
	 * {@link IStringProcessor} object that takes a String and processes it 
	 * or null
	 */
	public FileLineProcessorModule(IStringProcessor<A> processor) {
		this(processor, false);
	}
	
	/**
	 * Creates a new {@link FileLineProcessorModule} object with the given parameters.
	 * @param processor
	 * {@link IStringProcessor} object that takes a String and processes it
	 * @param abortOnError
	 * whether the execution should be aborted when encountering an error
	 */
	public FileLineProcessorModule(IStringProcessor<A> processor, boolean abortOnError) {
		super(true);
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
			Log.abort(this, "Not able to open/read file %s.", input.toString());
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
