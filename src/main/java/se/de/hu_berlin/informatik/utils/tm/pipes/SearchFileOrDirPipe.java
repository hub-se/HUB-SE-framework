/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.pipes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import se.de.hu_berlin.informatik.utils.fileoperations.AFileWalker;
import se.de.hu_berlin.informatik.utils.fileoperations.AFileWalker.Builder;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.tm.AbstractProcessor;
import se.de.hu_berlin.informatik.utils.tm.Producer;

/**
 * Pipe that searches for matching files or directories and submits them to a linked pipe.
 * 
 * @author Simon Heiden
 */
public class SearchFileOrDirPipe extends AbstractProcessor<Path,Path> {

	final private String pattern;

	private boolean searchDirectories = false;
	private boolean searchFiles = false;
	private boolean includeRootDir = false;
	
	private boolean skipAfterFind = false;
	
	private boolean relative = false;

	/**
	 * Creates a new {@link SearchFileOrDirPipe} object with the given parameters. 
	 * @param pattern
	 * the pattern that describes the files that should be processed by this file walker
	 */
	public SearchFileOrDirPipe(String pattern) {
		super();
		this.pattern = pattern;
	}
	
	/**
	 * Enables searching for files.
	 * @return
	 * this
	 */
	public SearchFileOrDirPipe searchForFiles() {
		this.searchFiles = true;
		return this;
	}
	
	/**
	 * Puts out paths relative to the input path.
	 * @return
	 * this
	 */
	public SearchFileOrDirPipe relative() {
		this.relative = true;
		return this;
	}
	
	/**
	 * Enables searching for directories.
	 * @return
	 * this
	 */
	public SearchFileOrDirPipe searchForDirectories() {
		this.searchDirectories = true;
		return this;
	}
	
	/**
	 * Skips recursion to subtree after found items.
	 * @return
	 * this
	 */
	public SearchFileOrDirPipe skipSubTreeAfterMatch() {
		this.skipAfterFind = true;
		return this;
	}
	
	/**
	 * Includes the root directory in the search. Only matters
	 * if directories are being searched.
	 * @return
	 * this
	 */
	public SearchFileOrDirPipe includeRootDir() {
		includeRootDir = false;
		return this;
	}
	
	

	@Override
	public void trackAndConsume(Path item) {
		//do not track input item
		consume(item);
	}

	@Override
	public Path processItem(Path start, Producer<Path> producer) {
		//build a new FileWalker
		AFileWalker.Builder builder = new Builder(pattern) {
			@Override
			public AFileWalker build() {
				return new FileWalker(this, producer);
			}
		};
		
		if (includeRootDir) {
			builder.includeRootDir();
		}
		if (searchDirectories) {
			builder.searchForDirectories();
		}
		if (searchFiles) {
			builder.searchForFiles();
		}
		if (skipAfterFind) {
			builder.skipSubTreeAfterMatch();
		}
		if (relative) {
			builder.relative();
		}

		AFileWalker walker = builder.build();
		delegateTrackingTo(walker);
		
		//traverse the file tree
		try {
			Files.walkFileTree(start, Collections.emptySet(), Integer.MAX_VALUE, walker);
		} catch (IOException e) {
			Log.abort(this, e, "IOException thrown.");
		}
		
//		Log.out(this, "submitted items: %d", walker.getNumberOfMatches());
		walker.delegateTrackingTo(this);
		return null;
	}
    
	public class FileWalker extends AFileWalker {

		private Producer<Path> producer;

		protected FileWalker(Builder builder, Producer<Path> producer) {
			super(builder);
			this.producer = producer;
		}

		@Override
		public void processMatchedFileOrDir(Path fileOrDir) {
			producer.produce(fileOrDir);
		}
		
	}

}
