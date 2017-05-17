/**
 * 
 */
package se.de.hu_berlin.informatik.utils.files.processors;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;
import se.de.hu_berlin.informatik.utils.processors.sockets.ProcessorSocket;

/**
 * Processor that reads a submitted file and processes each line with the given
 * instance of a class that implements the interface {@link StringProcessor}.
 * Based on the implementation of the given {@link StringProcessor}, results
 * are produced after each line, after each file and/or at some later point in time.
 * 
 * @author Simon Heiden
 * 
 * @param A
 * the type of the return objects of the used {@link StringProcessor}
 */
public class FileLineProcessor<A> extends AbstractProcessor<Path, A> {

	public static Charset[] charsets = { 
			StandardCharsets.UTF_8, StandardCharsets.ISO_8859_1, 
			StandardCharsets.US_ASCII, StandardCharsets.UTF_16,
			StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE};
	
	private StringProcessor<A> processor;
	
	private boolean abortOnError = false;
	
	private int skip = 0;
	private int max = 0;
	
	/**
	 * Creates a new {@link FileLineProcessor} object with the given parameters.
	 * Will continue execution if a line can't be processed or produces an error.
	 * @param processor
	 * {@link StringProcessor} object that takes a String and processes it 
	 * or null
	 */
	public FileLineProcessor(StringProcessor<A> processor) {
		this(processor, false);
	}
	
	public FileLineProcessor<A> skipFirstLines(int count) {
		skip = count;
		return this;
	}
	
	public FileLineProcessor<A> readMaxLines(int count) {
		max = count;
		return this;
	}
	
	/**
	 * Creates a new {@link FileLineProcessor} object with the given parameters.
	 * @param processor
	 * {@link StringProcessor} object that takes a String and processes it
	 * @param abortOnError
	 * whether the execution should be aborted when encountering an error
	 */
	public FileLineProcessor(StringProcessor<A> processor, boolean abortOnError) {
		super();
		this.processor = processor;
		this.abortOnError = abortOnError;
	}

	@Override
	public A processItem(Path input, ProcessorSocket<Path, A> socket) {
		//try opening the file with different charsets
		for (Charset charset : charsets) {
			//try opening the file
			try (BufferedReader reader = Files.newBufferedReader(input , charset)) {
				String line;
				while ((line = reader.readLine()) != null) {
					if (skip > 0) {
						--skip;
						if (max > 0) {
							--max;
							if (max == 0) {
								break;
							}
						}
						continue;
					}
					
					if (!processor.process(line)) {
						if (abortOnError) {
							Log.abort(this, "Processing line \"%s\" with %s was not successful.", line, processor.getClass().getSimpleName());
						} else {
							Log.warn(this, "Processing line \"%s\" with %s was not successful.", line, processor.getClass().getSimpleName());
						}
					} else {
						A temp = processor.getLineResult();
						if (temp != null) {
							socket.produce(temp);
						}
					}

					if (max > 0) {
						--max;
						if (max == 0) {
							break;
						}
					}
				}
				return processor.getFileResult();
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
	
	/**
	 * Provides a method that gets a {@link String} and processes
	 * it in some way. Also provides a method to obtain a result
	 * object in the end. Instances of implementing classes can
	 * be loaded into a {@link FileLineProcessor}.
	 * 
	 * @author Simon Heiden
	 *
	 * @param T
	 * the type of the returned objects
	 */
	public static interface StringProcessor<T> {

		/**
		 * Takes a {@link String} and processes it.
		 * @param line
		 * an input {@link String}
		 * @return
		 * true if the operation succeeded, false otherwise
		 */
		public boolean process(String line);
		
		/**
		 * This method gets called after completely processing a given file.
		 * In the default case, this method simply returns null.
		 * @return
		 * the result of processing the last file; null if no result exists
		 */
		default public T getFileResult() {
			return null;
		}
		
		/**
		 * This method gets called after processing each line of a given file.
		 * In the default case, this method simply returns null.
		 * @return
		 * the result of processing the last line; null if no result exists
		 */
		default public T getLineResult() {
			return null;
		}

		/**
		 * This method may be called after processing multiple files and
		 * returns a result based on so far collected items.
		 * In the default case, this method simply returns null.
		 * @return
		 * the result of previously collected items; null if no result exists
		 */
		default public T getResultFromCollectedItems(){
			return null;
		}
	}
	
}
