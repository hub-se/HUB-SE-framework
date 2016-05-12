/**
 * 
 */
package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author Simon
 *
 */
public class ClassPathParser {

	/** The unique elements of the classpath, as an ordered list. */
	private final ArrayList<File> classpathElements;

	/** The unique elements of the classpath, as a set. */
	private final HashSet<String> classpathElementsSet;

	
	
	public ClassPathParser() {
		super();
		this.classpathElements = new ArrayList<>();
		this.classpathElementsSet = new HashSet<>();
	}

	/** 
	 * Clear the classpath. 
	 */
	private void clearClasspath() {
	    classpathElements.clear();
	    classpathElementsSet.clear();
	}

	/** 
	 * Add a classpath element. 
	 */
	private void addClasspathElement(String pathElement) {
	    if (classpathElementsSet.add(pathElement)) {
	        final File file = new File(pathElement);
	        if (file.exists()) {
	            classpathElements.add(file);
	        }
	    }
	}

	/** 
	 * Parse the system classpath. 
	 * @return
	 * this {@link ClassPathParser} object (for method chaining)
	 */
	public ClassPathParser parseSystemClasspath() {
	    // Look for all unique classloaders.
	    // Keep them in an order that (hopefully) reflects the order in which class resolution occurs.
	    ArrayList<ClassLoader> classLoaders = new ArrayList<>();
	    HashSet<ClassLoader> classLoadersSet = new HashSet<>();
	    classLoadersSet.add(ClassLoader.getSystemClassLoader());
	    classLoaders.add(ClassLoader.getSystemClassLoader());
	    if (classLoadersSet.add(Thread.currentThread().getContextClassLoader())) {
	        classLoaders.add(Thread.currentThread().getContextClassLoader());
	    }
	    // Dirty method for looking for any other classloaders on the call stack
	    try {
	        // Generate stacktrace
	        throw new Exception();
	    } catch (Exception e) {
	        StackTraceElement[] stacktrace = e.getStackTrace();
	        for (StackTraceElement elt : stacktrace) {
	            try {
	                ClassLoader cl = Class.forName(elt.getClassName()).getClassLoader();
	                if (classLoadersSet.add(cl)) {
	                    classLoaders.add(cl);
	                }
	            } catch (ClassNotFoundException e1) {
	            }
	        }
	    }

	    // Get file paths for URLs of each classloader.
	    clearClasspath();
	    for (ClassLoader cl : classLoaders) {
	        if (cl != null) {
	            for (URL url : ((URLClassLoader) cl).getURLs()) {
	                if ("file".equals(url.getProtocol())) {
	                    addClasspathElement(url.getFile());
	                }
	            }
	        }
	    }
	    return this;
	}


	/**
	 * @return 
	 * list of unique elements on the classpath (directories and files) as File objects, preserving order.
	 * Class path elements that do not exist are not included in the list.
	 */
	public ArrayList<File> getUniqueClasspathElements() {
	    return classpathElements;
	}
	
	/**
	 * Adds the given element at the start of the class path.
	 * @param element
	 * a file or directory to add to the class path
	 * @return
	 * this {@link ClassPathParser} object (for method chaining)
	 */
	public ClassPathParser addElementAtStartOfClassPath(File element) {
		classpathElements.add(0, element);
		return this;
	}
	
	/**
	 * Adds the given element to the end of the class path.
	 * @param element
	 * a file or directory to add to the class path
	 * @return
	 * this {@link ClassPathParser} object (for method chaining)
	 */
	public ClassPathParser addElementToClassPath(File element) {
		classpathElements.add(element);
		return this;
	}
	
	/**
	 * @return
	 * a string that depicts the current parsed and possibly 
	 * modified class path. Single elements are separated by
	 * the system's path separator. (';' on Windows, ':' on 
	 * Unix-based systems) 
	 */
	public String getClasspath() {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (File element : classpathElements) {
			if (first) {
				first = false;
			} else {
				builder.append(File.pathSeparator);
			}
			builder.append(element.toString());
		}
	    return builder.toString();
	}
}
