/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.pipes;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Objects;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.APipe;

/**
 * Pipe that searches for matching files or directories and submits them to a linked pipe.
 * 
 * @author Simon Heiden
 */
public class SearchFileOrDirPipe extends APipe<Path,Path> implements FileVisitor<Path> {

	private PathMatcher matcher = null;
	
	private boolean searchDirectories = false;
	private boolean searchFiles = false;
	
	private boolean isFirst = true;
	
	/**
	 * Creates a new {@link SearchFileOrDirPipe} object with the given pattern.
	 * @param ignoreRootDir
	 * whether the root directory should be ignored
	 * @param searchDirectories 
	 * whether files shall be included in the search
	 * @param searchFiles 
	 * whether directories shall be included in the search
	 * @param pattern
	 * pattern to match the file paths against, for example "**&#47;*.{txt}"
	 */
	public SearchFileOrDirPipe(boolean ignoreRootDir, boolean searchDirectories, boolean searchFiles, String pattern) {
		this(ignoreRootDir, searchDirectories, searchFiles);
		this.matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
	}
	
	/**
	 * Creates a new {@link SearchFileOrDirPipe} object that returns ALL visited
	 * files.
	 * @param ignoreRootDir
	 * whether the root directory should be ignored
	 * @param searchDirectories 
	 * whether files shall be included in the search
	 * @param searchFiles 
	 * whether directories shall be included in the search
	 */
	public SearchFileOrDirPipe(boolean ignoreRootDir, boolean searchDirectories, boolean searchFiles) {
		super();
		if (!searchDirectories && !searchFiles) {
			Log.abort(this, "Neither searching for files nor for directories.");
		}
		this.searchDirectories = searchDirectories;
		this.searchFiles = searchFiles;
		
		if (!ignoreRootDir) {
			isFirst = false;
		}
	}
	
	/**
	 * Creates a new {@link SearchFileOrDirPipe} object with the given pattern. Ignores
	 * the root directory.
	 * @param searchDirectories 
	 * whether files shall be included in the search
	 * @param searchFiles 
	 * whether directories shall be included in the search
	 * @param pattern
	 * pattern to match the file names against, for example "*.{txt}"
	 */
	public SearchFileOrDirPipe(boolean searchDirectories, boolean searchFiles, String pattern) {
		this(true, searchDirectories, searchFiles, pattern);
	}
	
	/**
	 * Creates a new {@link SearchFileOrDirPipe} object that returns ALL visited
	 * files. Ignores the root directory.
	 * @param searchDirectories 
	 * whether files shall be included in the search
	 * @param searchFiles 
	 * whether directories shall be included in the search
	 */
	public SearchFileOrDirPipe(boolean searchDirectories, boolean searchFiles) {
		this(true, searchDirectories, searchFiles);
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public Path processItem(Path start) {
		//traverse the file tree
		try {
			Files.walkFileTree(start, Collections.emptySet(), Integer.MAX_VALUE, this);
		} catch (IOException e) {
			Log.abort(this, e, "IOException thrown.");
		}
		return null;
	}

	/**
	 * Convenience method to match the given path against the pattern.
	 * @param path
	 * path to be matched against the pattern.
	 * @return
	 * true if path is not null and matches the pattern.
	 */
	private boolean match(Path path) {
		return path != null && matcher.matches(path);
	}
	
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {		
		if (attrs.isDirectory()) {
			if (searchDirectories) {
				if (matcher == null) {
					submitProcessedItem(file);
				} else if (match(file.toAbsolutePath())) {
					submitProcessedItem(file);
//					Misc.out(file.toString());
				}
			}
		} else {
			if (searchFiles) {
				if (matcher == null) {
					submitProcessedItem(file);
				} else if (match(file.toAbsolutePath())) {
					submitProcessedItem(file);
//					Misc.out(file.toString());
				}
			}
		}
		return CONTINUE;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		if (!isFirst) {
			if (searchDirectories) {
				if (matcher == null) {
					submitProcessedItem(dir);
				} else if (match(dir.toAbsolutePath())) {
					submitProcessedItem(dir);
//					Misc.out(dir.toString());
				}
			}
		} else {
			isFirst = false;
		}
		return CONTINUE;
	}
	
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        Objects.requireNonNull(file);
        throw exc;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        Objects.requireNonNull(dir);
        if (exc != null)
            throw exc;
        return FileVisitResult.CONTINUE;
    }
}
