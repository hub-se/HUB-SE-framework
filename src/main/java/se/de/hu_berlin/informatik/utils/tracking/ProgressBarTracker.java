package se.de.hu_berlin.informatik.utils.tracking;

import java.io.IOException;

public class ProgressBarTracker implements ITrackingStrategy {

	final private static String BAR = "[--------------------]";
	final private static String ICON_POS = ">";
	final private static String ICON_NEG = "<";
	final private static int BAR_LENGTH = BAR.length();
	
	final private static int PROGRESS_BAR_MSG_LENGTH = 80 - BAR_LENGTH - 1;
	
	private boolean bouncePositive = true;
	private int barPosition = 0;
	
	private int stepWidth = 0;
	private int count = 0;
	
	public ProgressBarTracker(int stepWidth) {
		super();
		this.stepWidth = stepWidth;
	}

	@Override
	public void track() {
		if (++count % stepWidth == 0 || count == 1) {
			updateAndWriteTrackBar();
		}
	}
	
	@Override
	public void track(String msg) {
		if (++count % stepWidth == 0 || count == 1) {
			updateAndWriteTrackBar(msg);
		}
	}

	private void updateAndWriteTrackBar() {
		try {
			if (barPosition < BAR_LENGTH && barPosition > 0) {
				String b1 = BAR.substring(0, barPosition);
				String b2 = BAR.substring(barPosition);
				String icon;
				if(bouncePositive) {
					icon = ICON_POS;
					barPosition++;
				} else {
					icon = ICON_NEG;
					barPosition--;
				}
				System.out.write((b1 + icon + b2 + "\r").getBytes());
			}
			
			if (barPosition == BAR_LENGTH) {
				barPosition--;
				bouncePositive = false;
			} else if (barPosition == 0) {
				barPosition++;
				bouncePositive = true;
			}
		} catch (IOException e) { //do nothing
		}
	}
	
	private void updateAndWriteTrackBar(String msg) {
		try {
			if (barPosition < BAR_LENGTH && barPosition > 0) {
				String b1 = BAR.substring(0, barPosition);
				String b2 = BAR.substring(barPosition);
				String icon;
				if(bouncePositive) {
					icon = ICON_POS;
					barPosition++;
				} else {
					icon = ICON_NEG;
					barPosition--;
				}
				System.out.write((b1 + icon + b2 + " " + 
						generateTruncatedMessage(msg, PROGRESS_BAR_MSG_LENGTH) + "\r").getBytes());
			}
			
			if (barPosition == BAR_LENGTH) {
				barPosition--;
				bouncePositive = false;
			} else if (barPosition == 0) {
				barPosition++;
				bouncePositive = true;
			}
		} catch (IOException e) { //do nothing
		}
	}
	
}
