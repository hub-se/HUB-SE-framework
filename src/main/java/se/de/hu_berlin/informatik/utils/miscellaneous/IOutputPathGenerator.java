/**
 * 
 */
package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.nio.file.Path;

/**
 * Interface to provide unique, automatically generated output paths.
 * 
 * @author Simon Heiden
 * 
 * @param <A>
 * type of objects to support file naming
 */
public interface IOutputPathGenerator<A> {

	/**
	 * Gets a new, unique output path with the specified extension.
	 * @param extension
	 * holds an extension for the file to create (e.g. ".txt")
	 * @return
	 * a new {@link Path} to a non-existing file
	 */
	public Path getNewOutputPath(String extension);
	
	/**
	 * Gets a new, unique output path.
	 * @return
	 * a new {@link Path} to a non-existing file
	 */
	public Path getNewOutputPath();
	
	/**
	 * Gets a new, unique output path with the specified extension.
	 * @param someObject
	 * is an object that is used to generate an output path
	 * @param extension
	 * holds an extension for the file to create (e.g. ".txt")
	 * @return
	 * a new {@link Path} to a non-existing file
	 */
	default public Path getNewOutputPath(A someObject, String extension) {
		return getNewOutputPath(extension);
	}

}
