/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.modules;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AModule;

/**
 * Invokes the given method of the given class with the submitted arguments in a new thread
 * via reflection.
 * 
 * @author Simon Heiden
 */
public class InvokeMethodInNewThreadModule extends AModule<Object[],Thread> {

	private String className;
	private String methodName;
	private Object caller;
	private boolean waitForFinish;
	private Class<?> argType;
	
	public InvokeMethodInNewThreadModule(Object caller, final String className, final String methodName, Class<?> argType, boolean waitForFinish) {
		super(true);
		this.caller = caller;
		this.className = className;
		this.methodName = methodName;
		this.waitForFinish = waitForFinish;
		this.argType = argType;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public Thread processItem(Object[] args) {
		return start(args);
	}

	private Thread start(Object[] args) {
		// Create a new thread
		Thread thread = new Thread(new Runnable() {
			public void run() {
//				URLClassLoader cl = null;
				try {
					// create the custom class loader
//					cl = (URLClassLoader) args[0];
//					cl = new InjectingClassLoader(urls);
					// load the class
					Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(className);
//					Class<?> clazz = cl.loadClass(className);
//					Class<?>[] types = new Class<?>[args.length];
//					for (int i = 0; i < args.length; ++i) {
//						types[i] = argType;
//					}
					// get the method
					Method method = null;
					try {
					method = clazz.getDeclaredMethod(methodName, argType);
					} catch (NoSuchMethodException x) {
						for (Method m : clazz.getMethods()) {
							if (m.getName().equals(methodName)) {
								method = m;
								break;
							}
						}
						if (method == null) {
							throw x;
						}
					}
					// and invoke it 
					method.invoke(caller, new Object[] {args});
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					}
			}
		});
		
		//start the thread
		thread.start();
		
		if (waitForFinish) {
			Misc.waitOnThread(thread);
		}
		
		//return the started thread
		return thread;
	}
}
