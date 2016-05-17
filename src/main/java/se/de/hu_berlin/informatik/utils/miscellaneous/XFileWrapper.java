package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.nio.file.Path;

/**
 * @author SimHigh
 *
 * @param <A>
 * type that is iterable and whose elements extend {@link CharSequence}
 */
public class XFileWrapper<A extends Iterable<? extends CharSequence> > {

	private A linesToWrite;
	private Path outputPath;
	
	
	/**
	 * @param linesToWrite
	 * the lines to write to the output path
	 * @param outputPath
	 * the output path to write the lines to
	 */
	public XFileWrapper(A linesToWrite, Path outputPath) {
		super();
		this.linesToWrite = linesToWrite;
		this.outputPath = outputPath;
	}


	/**
	 * @return
	 * the lines to write to the output path
	 */
	public A getLinesToWrite() {
		return linesToWrite;
	}


	/**
	 * @return
	 * the output path to write the lines to
	 */
	public Path getOutputPath() {
		return outputPath;
	}
	
	
	
}
