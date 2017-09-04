package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import se.de.hu_berlin.informatik.utils.files.FileUtils;

public class ClassUtils {

	public static boolean copyClassesTo(Path output, Class<?>... classes) {
		if (output.toFile().isFile()) {
			Log.err(ClassUtils.class, "Can not copy class files to '%s'.", output);
			return false;
		}
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		for (Class<?> clazz : classes) {
			String className = clazz.getName();
			String classPath = className.replace('.', File.separatorChar) + ".class";
			Path target = output.resolve(classPath);
			Log.out(ClassUtils.class, "Extracting class '%s' to '%s'...", className, target);
			FileUtils.ensureParentDir(target.toFile());
			try (InputStream stream = contextClassLoader.getResourceAsStream(classPath)) {
				Files.copy(stream, target, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				Log.err(ClassUtils.class, e, "Extracting class '%s' to '%s' failed.", className, target);
			} catch (NullPointerException e) {
				Log.err(ClassUtils.class, e, "Class '%s' not found.", className, target);
			}
		}
		return true;
	}
}
