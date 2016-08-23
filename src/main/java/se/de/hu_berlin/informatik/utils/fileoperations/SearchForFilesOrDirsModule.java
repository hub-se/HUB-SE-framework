/**
 * 
 */
package se.de.hu_berlin.informatik.utils.fileoperations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import se.de.hu_berlin.informatik.utils.miscellaneous.SearchFileOrDirWalker;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AModule;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;

/**
 * Starts a file walker that searches for files and directories that 
 * match a given pattern, starting from a submitted input path.
 * 
 * @author Simon Heiden
 * 
 * @see SearchFileOrDirWalker
 */
public class SearchForFilesOrDirsModule extends AModule<Path,List<Path>> {

	private String pattern;
	private int depth = 0;
	
	private boolean searchDirectories = false;
	private boolean searchFiles = false;
	
	private boolean skipAfterFind = false;
	
	/**
	 * Creates a new {@link SearchForFilesOrDirsModule} object with the given parameter.
	 * @param searchForDirectories 
	 * whether files shall be included in the search
	 * @param searchForFiles 
	 * whether directories shall be included in the search
	 * @param pattern
	 * the pattern that the files are matched against
	 * (a value of null matches all files and dirs)
	 * @param skipAfterFind
	 * whether to skip subtree elements after a match (will only affect matching directories)
	 * @param recursive 
	 * whether files should be searched recursively
	 */
	public SearchForFilesOrDirsModule(boolean searchForDirectories, boolean searchForFiles, 
			String pattern, boolean skipAfterFind, boolean recursive) {
		this(searchForDirectories, searchForFiles, pattern, skipAfterFind, recursive ? Integer.MAX_VALUE : 1);
	}
	
	/**
	 * Creates a new {@link SearchForFilesOrDirsModule} object with the given parameter.
	 * @param searchForDirectories 
	 * whether files shall be included in the search
	 * @param searchForFiles 
	 * whether directories shall be included in the search
	 * @param pattern
	 * the pattern that the files are matched against
	 * (a value of null matches all files and dirs)
	 * @param skipAfterFind
	 * whether to skip subtree elements after a match (will only affect matching directories)
	 * @param depth
	 * maximum depth in which to search
	 */
	public SearchForFilesOrDirsModule(boolean searchForDirectories, boolean searchForFiles, 
			String pattern, boolean skipAfterFind, int depth) {
		super(true);
		this.pattern = pattern;
		this.searchDirectories = searchForDirectories;
		this.searchFiles = searchForFiles;
		this.skipAfterFind = skipAfterFind;
		if (depth < 0) {
			Log.abort(this, "Search depth has negative value.");
		}
		this.depth = depth;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public List<Path> processItem(Path input) {
		if (!input.toFile().exists()) {
			Log.abort(this, "Path '%s' doesn't exist.", input.toString());
		}
		
		//declare a search for files FileWalker
		SearchFileOrDirWalker walker;
		if (pattern == null) {
			walker = new SearchFileOrDirWalker(searchDirectories, searchFiles, skipAfterFind);
		} else {
			walker = new SearchFileOrDirWalker(searchDirectories, searchFiles, pattern, skipAfterFind);
		}
		delegateTrackingTo(walker);
		
		//traverse the file tree
		try {
			Files.walkFileTree(input, Collections.emptySet(), depth, walker);
		} catch (IOException e) {
			Log.abort(this, e, "IOException thrown.");
		}

		return walker.getResult();
	}

}
