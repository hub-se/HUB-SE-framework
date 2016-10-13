package se.de.hu_berlin.informatik.utils.fileoperations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;

final public class FileUtils {

	//suppress default constructor (class should not be instantiated)
    private FileUtils() {
    	throw new AssertionError();
    }
    
	/**
	 * Deletes the given file or directory (recursively).
	 * @param fileOrDir
	 * a file or a directory
	 * @return
	 * true if and only if the file or directory is successfully deleted; false otherwise
	 */
	public static boolean delete(final File fileOrDir) {
		if (fileOrDir.isDirectory()) {
			try {
				for (final File file : fileOrDir.listFiles()) {
					delete(file);
				}
			} catch(NullPointerException e) {
				Log.err(null, "Could not delete " + fileOrDir.toString() + ".");
			}
		}
		return fileOrDir.delete();
	}
	
	/**
	 * Deletes the given file or directory (recursively).
	 * @param fileOrDir
	 * a file or a directory
	 * @return
	 * true if and only if the file or directory is successfully deleted; false otherwise
	 */
	public static boolean delete(final Path fileOrDir) {
		return delete(fileOrDir.toFile());
	}
	
	/**
	 * Searches for a file containing the given pattern (recursively).
	 * @param startDir
	 * the starting directory
	 * @param pattern
	 * the pattern to search for
	 * @return
	 * the found file, or null if no file was found that contains the pattern
	 */
	public static File searchFileContainingPattern(final File startDir, final String pattern) {
		return searchFileContainingPattern(startDir, pattern, Integer.MAX_VALUE);
	}
	
	/**
	 * Searches for a file containing the given pattern up to a specified depth.
	 * @param startDir
	 * the starting directory
	 * @param pattern
	 * the pattern to search for
	 * @param depth
	 * recursion depth
	 * @return
	 * the found file, or null if no file was found that contains the pattern
	 */
	public static File searchFileContainingPattern(final File startDir, final String pattern, final int depth) {
		if (depth < 0) {
			return null;
		}
		if (startDir.isDirectory()) {
			if (depth == 0) {
				return null;
			}
			try {
				for (final File file : startDir.listFiles()) {
					final File result = searchFileContainingPattern(file, pattern, depth-1);
					if (result != null) {
						return result;
					}
				}
			} catch(NullPointerException e) {
				Log.err(null, "Could not search in " + startDir.toString() + ".");
			}
		} else if (startDir.getName().contains(pattern)) {
			return startDir;
		}
		
		return null;
	}
	
	/**
	 * Copies a file or a directory recursively.
	 * @param source
	 * the source file or directory
	 * @param dest
	 * the destination file or directory
	 * @param options
	 * the copy options
	 * @throws IOException
	 * thrown in case of an error
	 */
	public static void copyFileOrDir(
			final File source, final File dest, final CopyOption...  options) throws IOException {
	    if (source.isDirectory()) {
	        copyDir(source, dest, options);
	    } else {
	        ensureParentDir(dest);
	        copyFile(source, dest, options);
	    }
	}

	/**
	 * Copies a directory. 
	 * @param source
	 * the source directory
	 * @param dest
	 * the destination directory
	 * @param options
	 * the copy options
	 * @throws IOException
	 * thrown in case of an error
	 */
	private static void copyDir(
			final File source, final File dest, final CopyOption... options) throws IOException {
	    if (!dest.exists()) {
	        dest.mkdirs();
	    }
	    final File[] contents = source.listFiles();
	    if (contents != null) {
	        for (final File f : contents) {
	        	final File newFile = new File(dest.getAbsolutePath() + File.separator + f.getName());
	            if (f.isDirectory()) {
	                copyDir(f, newFile, options);
	            } else {
	                copyFile(f, newFile, options);
	            }
	        }
	    }
	}

	/**
	 * Copies a file.
	 * @param source
	 * the source file
	 * @param dest
	 * the destination file
	 * @param options
	 * the copy options
	 * @throws IOException
	 * thrown in case of an error
	 */
	private static void copyFile(
			final File source, final File dest, final CopyOption... options) throws IOException {
	    Files.copy(source.toPath(), dest.toPath(), options);
	}

	/**
	 * Ensure that the given file has a parent directory. Creates all
	 * directories on the way to the parent directory if they not exist.
	 * @param file
	 * the file
	 */
	public static void ensureParentDir(final File file) {
		final File parent = file.getParentFile();
	    if (parent != null && !parent.exists()) {
	        parent.mkdirs();
	    }
	}
	
	/**
	 * Writes a String to the provided file. If the file does not exist, it will be created.
	 * @param string
	 * the string to write
	 * @param file
	 * the output file
	 * @throws IOException
	 * if the file is a directory or can not be opened or written to
	 */
	public static void writeString2File(final String string, final File file) throws IOException {
		if (!file.exists()) {
			file.createNewFile();
		}
		try (final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
			writer.println(string);
		} catch (IOException e) {
			throw e;
		}
	}
	
	/**
	 * Appends a String to the provided file. If the file does not exist, it will be created.
	 * @param string
	 * the string to append
	 * @param file
	 * the output file
	 * @throws IOException
	 * if the file is a directory or can not be opened or written to
	 */
	public static void appendString2File(final String string, final File file) throws IOException {
		if (!file.exists()) {
			file.createNewFile();
		}
		try (final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
			writer.println(string);
		} catch (IOException e) {
			throw e;
		}
	}
	
	/**
	 * Reads a file and returns its contents as a String.
	 * @param path
	 * the path to the file to read
	 * @return
	 * the file's contents
	 * @throws IOException
	 * if the file does not exist or can not be opened
	 */
	public static String readFile2String(final Path path) throws IOException {
		return new String(Files.readAllBytes(path));
	}
	
	/**
	 * Reads a file and returns its contents as a String.
	 * @param path
	 * the path to the file to read
	 * @return
	 * the file's contents or null if it could not be opened
	 */
	public static List<String> readFile2List(final Path path) {
		return new FileToListModule().submit(path).getResult();
	}
	
	/**
	 * Reads a file and returns its contents as a char array.
	 * @param filePath
	 * the path to the file to read
	 * @return
	 * the file's contents
	 * @throws IOException
	 * if the file does not exist or can not be opened
	 */
	public static char[] readFile2CharArray(final String filePath) throws IOException {
		return new String(Files.readAllBytes(Paths.get(filePath))).toCharArray();
	}
	
}
