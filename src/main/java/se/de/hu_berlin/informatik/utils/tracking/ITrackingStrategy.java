package se.de.hu_berlin.informatik.utils.tracking;

import java.io.IOException;

public interface ITrackingStrategy {
	
	final public static String ANIM= "|/-\\";
	final public static int ANIM_LENGTH = ANIM.length();

	public void track();
	
	default public void writeTrackMsg(int count) {
		try {
			System.out.write(("\r" + ANIM.charAt(count % ANIM_LENGTH)  + " " + count).getBytes());
		} catch (IOException e) { //do nothing
		}
	}
	
}
