package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.net.URL;

import java.net.URLClassLoader;
import java.util.List;

public class ParentLastClassLoader extends URLClassLoader {

	private ChildClassLoader childClassLoader;

	public ParentLastClassLoader(List<URL> classpath, ClassLoader parent, boolean debug) {
		this(classpath.toArray(new URL[classpath.size()]), parent, debug);
	}

	public ParentLastClassLoader(URL[] classpath, ClassLoader parent, boolean debug) {
		super(new URL[0], parent);
		if (debug) {
			childClassLoader = new DebugChildClassLoader(classpath, new DetectClass(this.getParent()));
		} else {
			childClassLoader = new ChildClassLoader(classpath, new DetectClass(this.getParent()));
		}
	}

	public ParentLastClassLoader(List<URL> classpath, boolean debug) {
		this(classpath.toArray(new URL[classpath.size()]), Thread.currentThread().getContextClassLoader(), debug);
	}

	public ParentLastClassLoader(URL[] classpath, boolean debug) {
		this(classpath, Thread.currentThread().getContextClassLoader(), debug);
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

		public DebugChildClassLoader(URL[] urls, DetectClass realParent) {
			super(urls, realParent);
		}

		@Override
		public Class<?> findClass(String name) throws ClassNotFoundException {
			Log.out(this, "Loading class: '%s'...", name);
			Class<?> loaded = findloadedClassinSuper(name);
			if (loaded != null) {
				Log.out(this, "Found loaded class: '%s'.", name);
				Log.out(this, "Found loaded class path: '%s'.", 
						loaded.getResource(loaded.getSimpleName() + ".class"));
				return loaded;
			}

			try {
				loaded = findClassinSuper(name);
				Log.out(this, "Found class in given URLs: '%s'.", name);
				Log.out(this, "Found class path in given URLs: '%s'.",
						loaded.getResource(loaded.getSimpleName() + ".class"));
				return loaded;
			} catch (ClassNotFoundException e) {
				try {
					loaded = getRealParent().loadClass(name);
					Log.out(this, "Loaded class from parent: '%s'.", name);
					Log.out(this, "Loaded class path from parent: '%s'.",
							loaded.getResource(loaded.getSimpleName() + ".class"));
				} catch (ClassNotFoundException x) {
					Log.out(this, "Loading class from super: '%s'.", name);
					throw x;
				}
				return loaded;
			}
		}

	}

	private static class ChildClassLoader extends URLClassLoader {

		private DetectClass realParent;

		public DetectClass getRealParent() {
			return realParent;
		}

		public ChildClassLoader(URL[] urls, DetectClass realParent) {
			super(urls, null);
			this.realParent = realParent;
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

			try {
				return findClassinSuper(name);
			} catch (ClassNotFoundException e) {
				return realParent.loadClass(name);
			}
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
