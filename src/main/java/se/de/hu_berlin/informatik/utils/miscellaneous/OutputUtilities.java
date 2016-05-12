/**
 * 
 */
package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Provides methods to modify the application's output streams.
 * 
 * @author Simon
 */
public class OutputUtilities {

	private static PrintStream originalOut = System.out;
	private static PrintStream originalErr = System.err;
	
	private static PrintStream nullStream = new PrintStream(new NullPrintStream());
	
	/**
	 * Nullifies any output that is written to the standard output stream.
	 */
	public static void switchOffStdOut() {
		System.setOut(nullStream);
	}
	
	/**
	 * Nullifies any output that is written to the standard output stream.
	 * This can not be reverted.
	 */
	public static void switchOffStdOutFINAL() {
		System.setOut(nullStream);
		originalOut = nullStream;
	}
	
	/**
	 * Switches the standard output stream back to its original state.
	 */
	public static void switchOnStdOut() {
		System.setOut(originalOut);
	}
	
	/**
	 * Nullifies any output that is written to the standard error stream.
	 */
	public static void switchOffStdErr() {
		System.setErr(nullStream);
	}
	
	/**
	 * Nullifies any output that is written to the standard error stream.
	 * This can not be reverted.
	 */
	public static void switchOffStdErrFINAL() {
		System.setErr(nullStream);
		originalErr = nullStream;
	}
	
	/**
	 * Switches the standard error stream back to its original state.
	 */
	public static void switchOnStdErr() {
		System.setErr(originalErr);
	}
	
	/**
	 * Simple output stream that ignores any given input.
	 * 
	 * @author Simon
	 */
	private static class NullPrintStream extends OutputStream {
		@Override
		public void write(int b) {}
		@Override
		public void write(byte[] b, int off, int len) {}
	}
	
}

