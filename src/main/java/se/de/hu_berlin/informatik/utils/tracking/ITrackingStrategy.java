package se.de.hu_berlin.informatik.utils.tracking;

import java.io.IOException;

public interface ITrackingStrategy {
	
	final public static String ANIM= "|/-\\";
	final public static int ANIM_LENGTH = ANIM.length();
	
	final public static int MAX_PRINT_LENGTH = 79;

	public void track();
	
	public void track(String msg);
	
	default public void writeTrackMsg(int count) {
		try {
			System.out.write((" -" + count + "-\r").getBytes());
		} catch (IOException e) { //do nothing
		}
	}
	
	default public void writeTrackMsg(int count, String msg) {
		try {
			System.out.write((" -" + count + "- " + 
					generateTruncatedMessage(msg, MAX_PRINT_LENGTH - 4 - String.valueOf(count).length()) + "\r").getBytes());
		} catch (IOException e) { //do nothing
		}
	}
	
	default public String generateTruncatedMessage(String msg, int length) {
		int msgLength = msg.length();
		if (msgLength > length) {
			return msg.substring(0, length);
		} else if (msgLength < length) {
			return msg + new String(new char[length - msgLength]);
		} else {
			return msg;
		}
	}

	public void reset();
}
