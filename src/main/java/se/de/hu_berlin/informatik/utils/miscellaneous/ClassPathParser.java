/**
 * 
 */
package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Simon
 *
 */
public class ClassPathParser {

	/** The unique elements of the classpath, as an ordered list. */
	private final List<URL> classpathElements;

	/** The unique elements of the classpath, as a set. */
	private final Set<String> classpathElementsSet;

	
	
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
	 * @param pathElement
	 * the path element to add
	 */
	private void addClasspathElement(final URL pathElement) {
	    if (classpathElementsSet.add(pathElement.toString())) {
	        final File file = new File(pathElement.getPath());
	        if (file.exists()) {
	        	classpathElements.add(pathElement);
	        } else {
	        	Log.err(this, "'%s' does not exist and is not added to class path.", file);
	        }
	    }
	}
	
	/** 
	 * Add a classpath element.
	 * @param pathElement
	 * the path element to add
	 */
	private void addClasspathElement(final File pathElement) {
		try {
        	addClasspathElement(pathElement.toURI().toURL());
		} catch (MalformedURLException e) {
			Log.err(this, "'%s' can not be transformed to a valid URL.", pathElement);
		}
	}
	
	/** 
	 * Add a classpath element.
	 * @param pathElement
	 * the path element to add
	 */
	private void addClasspathElement(final String pathElement) {
		addClasspathElement(new File(pathElement));
	}
	
	/** 
	 * Add a classpath element at the start.
	 * @param pathElement
	 * the path element to add
	 */
	private void addClasspathElementAtStart(final URL pathElement) {
	    if (classpathElementsSet.add(pathElement.toString())) {
	        final File file = new File(pathElement.getPath());
	        if (file.exists()) {
	        	classpathElements.add(0, pathElement);
	        } else {
	        	Log.err(this, "'%s' does not exist and is not added to class path.", file);
	        }
	    }
	}
	
	/** 
	 * Add a classpath element at the start.
	 * @param pathElement
	 * the path element to add
	 */
	private void addClasspathElementAtStart(final File pathElement) {
		try {
        	addClasspathElementAtStart(pathElement.toURI().toURL());
		} catch (MalformedURLException e) {
			Log.err(this, "'%s' can not be transformed to a valid URL.", pathElement);
		}
	}
	
	/** 
	 * Add a classpath element at the start.
	 * @param pathElement
	 * the path element to add
	 */
	private void addClasspathElementAtStart(final String pathElement) {
		addClasspathElementAtStart(new File(pathElement));
	}

	
	/** 
	 * Parse the system classpath. 
	 * @return
	 * this {@link ClassPathParser} object (for method chaining)
	 */
	public ClassPathParser parseSystemClasspath() {
	    // Look for all unique classloaders.
	    // Keep them in an order that (hopefully) reflects the order in which class resolution occurs.
		final List<ClassLoader> classLoaders = new ArrayList<>();
		final Set<ClassLoader> classLoadersSet = new HashSet<>();
	    classLoadersSet.add(ClassLoader.getSystemClassLoader());
	    classLoaders.add(ClassLoader.getSystemClassLoader());
	    if (classLoadersSet.add(Thread.currentThread().getContextClassLoader())) {
	        classLoaders.add(Thread.currentThread().getContextClassLoader());
	    }
	    // Dirty method for looking for any other classloaders on the call stack
	    try {
	        // Generate stacktrace
	        throw new IllegalAccessException();
	    } catch (IllegalAccessException e) {
	    	final StackTraceElement[] stacktrace = e.getStackTrace();
	        for (final StackTraceElement elt : stacktrace) {
	            try {
	            	final ClassLoader cl = Class.forName(elt.getClassName()).getClassLoader();
	                if (classLoadersSet.add(cl)) {
	                    classLoaders.add(cl);
	                }
	            } catch (ClassNotFoundException e1) {
	            }
	        }
	    }

	    // Get file paths for URLs of each classloader.
	    clearClasspath();
	    for (final ClassLoader cl : classLoaders) {
	        if (cl != null) {
	        	if (cl instanceof URLClassLoader) {
	        		for (final URL url : ((URLClassLoader) cl).getURLs()) {
	        			if ("file".equals(url.getProtocol())) {
	        				addClasspathElement(url.getFile());
	        			} else {
	        				Log.warn(this, "'%s' was not added to class path with protocol '%s'", url, url.getProtocol());
	        			}
	        		}
	        	} else {
	        		Log.warn(this, "Could not get URLs from a class loader.");
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
	public List<URL> getUniqueClasspathElements() {
	    return classpathElements;
	}
	
	/**
	 * Adds the given element at the start of the class path.
	 * @param element
	 * a file or directory to add to the class path
	 * @return
	 * this {@link ClassPathParser} object (for method chaining)
	 */
	public ClassPathParser addElementAtStartOfClassPath(final String element) {
		addClasspathElementAtStart(element);
		return this;
	}
	
	/**
	 * Adds the given element at the start of the class path.
	 * @param element
	 * a file or directory to add to the class path
	 * @return
	 * this {@link ClassPathParser} object (for method chaining)
	 */
	public ClassPathParser addElementAtStartOfClassPath(final File element) {
		addClasspathElementAtStart(element);
		return this;
	}
	
	/**
	 * Adds the given element at the start of the class path.
	 * @param element
	 * a URL to add to the class path
	 * @return
	 * this {@link ClassPathParser} object (for method chaining)
	 */
	public ClassPathParser addElementAtStartOfClassPath(final URL element) {
		addClasspathElementAtStart(element);
		return this;
	}
	
//	/**
//	 * Adds the given class path to the start of the class path.
//	 * @param cp
//	 * a class path to add to the class path
//	 * @return
//	 * this {@link ClassPathParser} object (for method chaining)
//	 */
//	public ClassPathParser addClassPathToStartOfClassPath(final String cp) {
//		String[] pathElements = cp.split(File.pathSeparator);
//		for (String element : pathElements) {
//			addElementAtStartOfClassPath(new File(element));
//		}
//		return this;
//	}
	
	/**
	 * Adds the given element to the end of the class path.
	 * @param element
	 * a file or directory to add to the class path
	 * @return
	 * this {@link ClassPathParser} object (for method chaining)
	 */
	public ClassPathParser addElementToClassPath(final String element) {
		addClasspathElement(element);
		return this;
	}
	
	/**
	 * Adds the given element to the end of the class path.
	 * @param element
	 * a file or directory to add to the class path
	 * @return
	 * this {@link ClassPathParser} object (for method chaining)
	 */
	public ClassPathParser addElementToClassPath(final File element) {
		addClasspathElement(element);
		return this;
	}
	
	/**
	 * Adds the given element to the end of the class path.
	 * @param element
	 * a URL to add to the class path
	 * @return
	 * this {@link ClassPathParser} object (for method chaining)
	 */
	public ClassPathParser addElementToClassPath(final URL element) {
		addClasspathElement(element);
		return this;
	}
	
	/**
	 * Adds the given class path to the end of the class path.
	 * @param cp
	 * a class path to add to the class path
	 * @return
	 * this {@link ClassPathParser} object (for method chaining)
	 */
	public ClassPathParser addClassPathToClassPath(final String cp) {
		String[] pathElements = cp.split(File.pathSeparator);
		for (String element : pathElements) {
			addClasspathElement(new File(element));
		}
		return this;
	}
	
	/**
	 * @return
	 * a string that depicts the current parsed - and/or possibly 
	 * modified - class path. Single elements are separated by
	 * the system's path separator. (';' on Windows, ':' on 
	 * Unix-based systems)
	 */
	public String getClasspath() {
		final StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (final URL element : classpathElements) {
			if (first) {
				first = false;
			} else {
				builder.append(File.pathSeparator);
			}
			builder.append(new File(element.getPath()).toPath().toAbsolutePath().toString());
		}
	    return builder.toString();
	}
	
	/**
	 * Returns a URLClassLoader containing the URLs provided to this
	 * ClassPathParser that has the given parent ClassLoader.
	 * @param parent
	 * the parent for the returned Classloader
	 * @return
	 * a ClassLoader that knows the paths provided to him
	 */
	public ClassLoader getClassLoader(ClassLoader parent) {
		URL[] urls = getUniqueClasspathElements().toArray(new URL[getUniqueClasspathElements().size()]);
		
		return new URLClassLoader(urls, parent);
	}
	
	/**
	 * Returns a URLClassLoader containing the URLs provided to this
	 * ClassPathParser that has the given parent ClassLoader. The returned
	 * ClassLoader first searches in the given URLs for classes, before
	 * querying the given parent ClassLoader.
	 * @param parent
	 * the parent for the returned Classloader
	 * @return
	 * a ClassLoader that knows the paths provided to him
	 */
	public ClassLoader getParentLastClassLoader(ClassLoader parent) {
		URL[] urls = getUniqueClasspathElements().toArray(new URL[getUniqueClasspathElements().size()]);
		
		return new ParentLastClassLoader(urls, parent, false);
	}
	
}
