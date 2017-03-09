package se.de.hu_berlin.informatik.utils.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;

import se.de.hu_berlin.informatik.utils.files.processors.FileToStringListReader;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;

final public class FileUtils {

	//suppress default constructor (class should not be instantiated)
    private FileUtils() {
    	throw new AssertionError();
    }
    
    private static Path getCompletePath(final Path prefix, final String filePath) {
    	Objects.requireNonNull(filePath);
		Path path;
		if (prefix != null) {
			path = prefix.resolve(Paths.get(filePath));
		} else {
			path = Paths.get(filePath);
		}
		return path;
	}
    
    /**
     * Checks whether the given path points to a (possibly not yet existing) file 
     * (i.e. NOT to an existing directory).
     * @param prefix
     * a prefix path, or null if the given file path is not relative
     * @param filePath
     * the path to check if it points to a file
     * @return
     * the complete path to the (possibly not yet existing) file, or null if the path 
     * would point to an existing directory
     */
    public static Path checkIfNotAnExistingDirectory(final Path prefix, 
    		final String filePath) {
		Path path = getCompletePath(prefix, filePath);
		
		if (path.toFile().isDirectory()) {
			return null;
		} else {
			return path;
		}
	}
    
    /**
     * Checks whether the given path points to an existing file.
     * @param prefix
     * a prefix path, or null if the given file path is not relative
     * @param filePath
     * the path to check if it points to a file
     * @return
     * the complete path to the file, or null if the path 
     * would point to a directory or a file that does not exist
     */
    public static Path checkIfAnExistingFile(final Path prefix, 
    		final String filePath) {
		Path path = getCompletePath(prefix, filePath);
		
		if (path.toFile().isFile()) {
			return path;
		} else {
			return null;
		}
	}

	
    
    /**
     * Checks whether the given path doesn't point to an existing file. The returned
     * path may either point to an existing directory or to a non-existing element.
     * The returned path is null, if the given path points to an existing file.
     * @param prefix
     * a prefix path, or null if the given path is not relative
     * @param dirPath
     * the path to check if it points not to an existing file
     * @return
     * the complete path, or null if the path 
     * would point to an existing file
     */
    public static Path checkIfNotAnExistingFile(final Path prefix, 
    		final String dirPath) {
		Path path = getCompletePath(prefix, dirPath);
		
		if (path.toFile().isFile()) {
			return null;
		} else {
			return path;
		}
	}
    
    /**
     * Checks whether the given path points to an existing directory
     * and returns the path if it points to an existing directory, or
     * null otherwise.
     * @param prefix
     * a prefix path, or null if the given path is not relative
     * @param dirPath
     * the path to check if it points to an existing directory
     * @return
     * the complete path to the directory if it exists, 
     * or null otherwise
     */
    public static Path checkIfAnExistingDirectory(final Path prefix, 
    		final String dirPath) {
		Path path = getCompletePath(prefix, dirPath);
		
		if (path.toFile().isDirectory()) {
			return path;
		} else {
			return null;
		}
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
	 * Searches for a directory containing the given pattern (recursively).
	 * @param startDir
	 * the starting directory
	 * @param pattern
	 * the pattern to search for
	 * @return
	 * the found directory, or null if no directory was found that contains the pattern
	 */
	public static File searchDirectoryContainingPattern(final File startDir, final String pattern) {
		return searchDirectoryContainingPattern(startDir, pattern, Integer.MAX_VALUE);
	}
	
	/**
	 * Searches for a directory containing the given pattern up to a specified depth.
	 * @param startDir
	 * the starting directory
	 * @param pattern
	 * the pattern to search for
	 * @param depth
	 * recursion depth
	 * @return
	 * the found directory, or null if no directory was found that contains the pattern
	 */
	public static File searchDirectoryContainingPattern(final File startDir, final String pattern, final int depth) {
		if (depth < 0) {
			return null;
		}
		if (startDir.isDirectory()) {
			if (startDir.getName().contains(pattern)) {
				return startDir;
			}
			if (depth == 0) {
				return null;
			}
			try {
				for (final File file : startDir.listFiles()) {
					final File result = searchDirectoryContainingPattern(file, pattern, depth-1);
					if (result != null) {
						return result;
					}
				}
			} catch(NullPointerException e) {
				Log.err(null, "Could not search in " + startDir.toString() + ".");
			}
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
			ensureParentDir(file);
			file.createNewFile();
		}
		try (final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
			writer.println(string);
		} catch (IOException e) {
			throw e;
		}
	}
	
	/**
	 * Writes the given Strings to the provided file. If the file does not exist, it will be created.
	 * @param strings
	 * the strings to write
	 * @param file
	 * the output file
	 * @throws IOException
	 * if the file is a directory or can not be opened or written to
	 */
	public static void writeStrings2File(final File file, final String... strings) throws IOException {
		if (strings.length < 1) {
			Log.warn(FileUtils.class, "No Strings given to write to file. Only creating an empty file...");
		}
		
		if (!file.exists()) {
			ensureParentDir(file);
			file.createNewFile();
		}
		try (final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
			for (String string : strings) {
				writer.println(string);
			}
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
			ensureParentDir(file);
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
		return new FileToStringListReader().asModule().submit(path).getResult();
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
	
	/**
	 * Downloads the file that is given by the URL and writes it to the output file.
	 * @param webSiteURL
	 * a download URL as a String
	 * @param output
	 * the output file
	 * @return
	 * true if successful, false otherwise
	 */
	public static boolean downloadFile(String webSiteURL, File output) {
		try {
			return downloadFile(new URL(webSiteURL), output);
		} catch (MalformedURLException e) {
			Log.err(FileUtils.class, e);
			return false;
		}
	}
	
	/**
	 * Downloads the file that is given by the URL and writes it to the output file.
	 * @param webSiteURL
	 * a download URL
	 * @param output
	 * the output file
	 * @return
	 * true if successful, false otherwise
	 */
	public static boolean downloadFile(URL webSiteURL, File output) {
		try (FileOutputStream fos = new FileOutputStream(output)) {
			fos.getChannel().transferFrom(
					Channels.newChannel(webSiteURL.openStream()),
					0, Long.MAX_VALUE);
		} catch (Exception e) {
			Log.err(FileUtils.class, e, "Could not download: '%s'.", webSiteURL);
			return false;
		}
		
		return true;
	}
	
	/**
     * Returns the file extension of a file.
     * @param file 
     * to get extension of
     * @return 
     * file extension
     */
    public static String getFileExtension(final File file) {
        return getFileExtension(file.getName());
    }

    /**
     * Returns the file extension of a file.
     * @param file 
     * to get extension of
     * @return 
     * file extension
     */
    public static String getFileExtension(final String file) {
        return FilenameUtils.getExtension(file);
    }
    
    /**
     * Returns the file name without an existing file extension.
     * @param file 
     * to get without extension
     * @return 
     * file name without extension
     */
    public static String getFileNameWithoutExtension(final String file) {
    	return FilenameUtils.removeExtension(new File(file).getName());
    }
    
    /**
     * Returns the file path without an existing file extension.
     * @param file 
     * to get without extension
     * @return 
     * file path without extension
     */
    public static String getFileWithoutExtension(final String file) {
    	return FilenameUtils.removeExtension(file);
    }
	
}
