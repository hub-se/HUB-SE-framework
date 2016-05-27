/**
 * 
 */
package se.de.hu_berlin.informatik.utils.fileoperations;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import se.de.hu_berlin.informatik.utils.tm.moduleframework.AModule;
import se.de.hu_berlin.informatik.utils.tm.modules.stringprocessor.IStringProcessor;
import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;

/**
 * Module that reads a submitted file and processes each line with the given
 * instance of a class that implements the interface {@link IStringProcessor}.
 * In the end, a result object is returned to the output.
 * 
 * <br><br> Returns null in case of an error.
 * 
 * @author Simon Heiden
 */
public class FileLineProcessorModule<A> extends AModule<Path, A> {

	private IStringProcessor processor;
	private boolean abortOnError = false;
	
	/**
	 * Creates a new {@link FileLineProcessorModule} object with the given parameters.
	 * Will continue execution if a line can't be processed or produces an error.
	 * @param processor
	 * {@link IStringProcessor} object that takes a String and processes it 
	 * or null
	 */
	public FileLineProcessorModule(IStringProcessor processor) {
		this(processor, false);
	}
	
	/**
	 * Creates a new {@link FileLineProcessorModule} object with the given parameters.
	 * @param processor
	 * {@link IStringProcessor} object that takes a String and processes it
	 * @param abortOnError
	 * whether the execution should be aborted when encountering an error
	 */
	public FileLineProcessorModule(IStringProcessor processor, boolean abortOnError) {
		super(true);
		this.processor = processor;
		this.abortOnError = abortOnError;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public A processItem(Path input) {
		//try opening the file
		try (BufferedReader reader = Files.newBufferedReader(input , StandardCharsets.UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (!processor.process(line)) {
					if (abortOnError) {
						Misc.abort(this, "Processing line \"%s\" with %s was not successful.", line, processor.getClass().getSimpleName());
					} else {
						Misc.err(this, "Processing line \"%s\" with %s was not successful.", line, processor.getClass().getSimpleName());
					}
				}
			}
			
			return (A) processor.getResult();
			
		} catch (IOException x) {
			Misc.err(this, x, "Not able to open/read file %s.", input.toString());
		} catch (ClassCastException x) {
			Misc.abort(this, x, "Could not cast output object to desired type.");
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#getResultFromCollectedItems()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public A getResultFromCollectedItems() {
		return (A)processor.getResultFromCollectedItems();
	}

	
	
}
