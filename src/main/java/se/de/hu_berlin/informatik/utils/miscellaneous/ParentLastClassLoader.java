package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.net.URL;

import java.net.URLClassLoader;
import java.util.List;

public class ParentLastClassLoader extends URLClassLoader {

	private ChildClassLoader childClassLoader;

	public ParentLastClassLoader(List<URL> classpath, ClassLoader parent, boolean debug, String... excludes) {
		this(classpath.toArray(new URL[0]), parent, debug, (String[]) excludes);
	}

	public ParentLastClassLoader(URL[] classpath, ClassLoader parent, boolean debug, String... excludes) {
		super(new URL[0], parent);
		if (debug) {
			childClassLoader = new DebugChildClassLoader(classpath, new DetectClass(this.getParent()), (String[]) excludes);
		} else {
			childClassLoader = new ChildClassLoader(classpath, new DetectClass(this.getParent()), (String[]) excludes);
		}
	}

	public ParentLastClassLoader(List<URL> classpath, boolean debug, String... excludes) {
		this(classpath.toArray(new URL[0]), Thread.currentThread().getContextClassLoader(), debug, excludes);
	}

	public ParentLastClassLoader(URL[] classpath, boolean debug, String... excludes) {
		this(classpath, Thread.currentThread().getContextClassLoader(), debug, excludes);
	}

	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		try {
			return childClassLoader.findClass(name);
		} catch (ClassNotFoundException e) {
			return super.loadClass(name, resolve);
		}
	}

	private static class DebugChildClassLoader extends ChildClassLoader {

		public DebugChildClassLoader(URL[] urls, DetectClass realParent, String... excludes) {
			super(urls, realParent, (String[]) excludes);
		}

		@Override
		public Class<?> findClass(String name) throws ClassNotFoundException {
			System.out.println("Loading class: '" + name + "'...");
			Class<?> loaded = findloadedClassinSuper(name);
			if (loaded != null) {
				System.out.println("Found loaded class: '" + name + "'.");
				System.out.println("Found loaded class path: '" + loaded.getResource(loaded.getSimpleName() + ".class") + "'.");
				return loaded;
			}
			
			if (isExcluded(name)) {
				System.out.println("Class excluded: '" + name + "'.");
				try {
					loaded = getRealParent().loadClass(name);
					System.out.println("Loaded class from parent: '" + name + "'.");
					System.out.println("Loaded class path from parent: '" + loaded.getResource(loaded.getSimpleName() + ".class") + "'.");
				} catch (ClassNotFoundException x) {
					System.out.println("Loading class from super: '" + name + "'.");
					throw x;
				}
				return loaded;
			}

			try {
				loaded = findClassinSuper(name);
				System.out.println("Found class in given URLs: '" + name + "'.");
				System.out.println("Found class path in given URLs: '" + loaded.getResource(loaded.getSimpleName() + ".class") + "'.");
				return loaded;
			} catch (ClassNotFoundException e) {
				try {
					loaded = getRealParent().loadClass(name);
					System.out.println("Loaded class from parent: '" + name + "'.");
					System.out.println("Loaded class path from parent: '" + loaded.getResource(loaded.getSimpleName() + ".class") + "'.");
				} catch (ClassNotFoundException x) {
					System.out.println("Loading class from super: '" + name + "'.");
					throw x;
				}
				return loaded;
			}
		}

	}

	private static class ChildClassLoader extends URLClassLoader {

		private DetectClass realParent;
		private String[] excludes;

		public DetectClass getRealParent() {
			return realParent;
		}

		public ChildClassLoader(URL[] urls, DetectClass realParent, String... excludes) {
			super(urls, null);
			this.realParent = realParent;
			this.excludes = excludes;
		}

		public Class<?> findClassinSuper(String name) throws ClassNotFoundException {
			return super.findClass(name);
		}

		public Class<?> findloadedClassinSuper(String name) {
			return super.findLoadedClass(name);
		}

		@Override
		public Class<?> findClass(String name) throws ClassNotFoundException {
			Class<?> loaded = findloadedClassinSuper(name);
			if (loaded != null) {
				return loaded;
			}
			
			if (isExcluded(name)) {
				return realParent.loadClass(name);
			}

			try {
				return findClassinSuper(name);
			} catch (ClassNotFoundException e) {
				return realParent.loadClass(name);
			}
		}

		public boolean isExcluded(String name) {
			for (String exclude : excludes) {
				if (name.contains(exclude)) {
					return true;
				}
			}
			return false;
		}

	}

	private static class DetectClass extends ClassLoader {

		public DetectClass(ClassLoader parent) {
			super(parent);
		}

		@Override
		public Class<?> findClass(String name) throws ClassNotFoundException {
			return super.findClass(name);
		}

	}

}
