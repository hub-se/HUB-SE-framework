/**
 * 
 */
package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.FileVisitResult.*;

import java.io.IOException;

/**
 * {@link SimpleFileVisitor} extension that searches for and returns files
 * whose file paths match the given pattern.
 * 
 * @author Simon Heiden
 * 
 * @see SimpleFileVisitor
 */
public class SearchFileOrDirWalker extends SimpleFileVisitor<Path> {
	
	private PathMatcher matcher = null;
	
	private List<Path> matchedPaths;
	
	private boolean searchDirectories = false;
	private boolean searchFiles = false;
	
	private boolean isFirst = true;
	
	/**
	 * Creates a new {@link SearchFileOrDirWalker} object with the given pattern.
	 * @param ignoreRootDir
	 * whether the root directory should be ignored
	 * @param searchDirectories 
	 * whether files shall be included in the search
	 * @param searchFiles 
	 * whether directories shall be included in the search
	 * @param pattern
	 * pattern to match the file paths against, for example "**&#47;*.{txt}"
	 */
	public SearchFileOrDirWalker(boolean ignoreRootDir, boolean searchDirectories, boolean searchFiles, String pattern) {
		this(ignoreRootDir, searchDirectories, searchFiles);
		this.matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
	}
	
	/**
	 * Creates a new {@link SearchFileOrDirWalker} object that returns ALL visited
	 * files.
	 * @param ignoreRootDir
	 * whether the root directory should be ignored
	 * @param searchDirectories 
	 * whether files shall be included in the search
	 * @param searchFiles 
	 * whether directories shall be included in the search
	 */
	public SearchFileOrDirWalker(boolean ignoreRootDir, boolean searchDirectories, boolean searchFiles) {
		super();
		if (!searchDirectories && !searchFiles) {
			Log.abort(this, "Neither searching for files nor for directories.");
		}
		this.searchDirectories = searchDirectories;
		this.searchFiles = searchFiles;
		this.matchedPaths = new ArrayList<>();
		
		if (!ignoreRootDir) {
			isFirst = false;
		}
	}
	
	/**
	 * Creates a new {@link SearchFileOrDirWalker} object with the given pattern. Ignores
	 * the root directory.
	 * @param searchDirectories 
	 * whether files shall be included in the search
	 * @param searchFiles 
	 * whether directories shall be included in the search
	 * @param pattern
	 * pattern to match the file names against, for example "*.{txt}"
	 */
	public SearchFileOrDirWalker(boolean searchDirectories, boolean searchFiles, String pattern) {
		this(true, searchDirectories, searchFiles, pattern);
	}
	
	/**
	 * Creates a new {@link SearchFileOrDirWalker} object that returns ALL visited
	 * files. Ignores the root directory.
	 * @param searchDirectories 
	 * whether files shall be included in the search
	 * @param searchFiles 
	 * whether directories shall be included in the search
	 */
	public SearchFileOrDirWalker(boolean searchDirectories, boolean searchFiles) {
		this(true, searchDirectories, searchFiles);
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
	
	/**
	 * @return
	 * a list of matching files that can be obtained after using the file walker
	 */
	public List<Path> getResult() {
		return matchedPaths;
	}
	
	/* (non-Javadoc)
	 * @see java.nio.file.SimpleFileVisitor#visitFile(java.lang.Object, java.nio.file.attribute.BasicFileAttributes)
	 */
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {		
		if (attrs.isDirectory()) {
			if (searchDirectories) {
				if (matcher == null) {
					matchedPaths.add(file);
				} else if (match(file.toAbsolutePath())) {
					matchedPaths.add(file);
//					Misc.out(file.toString());
				}
			}
		} else {
			if (searchFiles) {
				if (matcher == null) {
					matchedPaths.add(file);
				} else if (match(file.toAbsolutePath())) {
					matchedPaths.add(file);
//					Misc.out(file.toString());
				}
			}
		}
		return CONTINUE;
	}

	/* (non-Javadoc)
	 * @see java.nio.file.SimpleFileVisitor#preVisitDirectory(java.lang.Object, java.nio.file.attribute.BasicFileAttributes)
	 */
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		if (!isFirst) {
			if (searchDirectories) {
				if (matcher == null) {
					matchedPaths.add(dir);
				} else if (match(dir.toAbsolutePath())) {
					matchedPaths.add(dir);
//					Misc.out(dir.toString());
				}
			}
		} else {
			isFirst = false;
		}
		return CONTINUE;
	}

	

	
}
