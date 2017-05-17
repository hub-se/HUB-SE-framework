package se.de.hu_berlin.informatik.utils.tracking;

import java.io.IOException;

@Deprecated
public class ProgressBarTracker implements TrackingStrategy {

	final private static String ICON_POS = ">";
	final private static String ICON_NEG = "<";
	
	final private static char BAR_OPEN = '[';
	final private static char BAR_CLOSE = ']';
	final private static char BAR_MIDDLE = '-';
	
	private String bar = "[--------------------]";
	private int barLength = bar.length();
	
	private int progressBarMsgLength = 80 - barLength - 4;
	
	private boolean bouncePositive = true;
	private int barPosition = 1;
	
	private int stepWidth = 0;
	private int count = 0;
	
	public ProgressBarTracker(final int stepWidth) {
		super();
		this.stepWidth = stepWidth;
	}
	
	public ProgressBarTracker(final int stepWidth, final int barLength) {
		super();
		this.stepWidth = stepWidth;
		char[] temp = new char[barLength+1];
		temp[0] = BAR_OPEN;
		temp[barLength] = BAR_CLOSE;
		for (int i = 1; i < barLength; ++i) {
			temp[i] = BAR_MIDDLE;
		}
		bar = new String(temp);
		this.barLength = bar.length();
		progressBarMsgLength = 80 - barLength - 4;
	}

	@Override
	public void track() {
		if (++count % stepWidth == 0 || count == 1) {
			updateAndWriteTrackBar();
		}
	}
	
	@Override
	public void track(final String msg) {
		if (++count % stepWidth == 0 || count == 1) {
			updateAndWriteTrackBar(msg);
		}
	}

	private void updateAndWriteTrackBar() {
		try {
			if (barPosition < barLength && barPosition > 0) {
				final String b1 = bar.substring(0, barPosition);
				final String b2 = bar.substring(barPosition);
				final String icon;
				if(bouncePositive) {
					icon = ICON_POS;
					barPosition++;
				} else {
					icon = ICON_NEG;
					barPosition--;
				}
				System.out.write((" " + b1 + icon + b2 + "\r").getBytes());
			}
			
			if (barPosition == barLength) {
				barPosition--;
				bouncePositive = false;
			} else if (barPosition == 0) {
				barPosition++;
				bouncePositive = true;
			}
		} catch (IOException e) { //do nothing
		}
	}
	
	private void updateAndWriteTrackBar(final String msg) {
		try {
			if (barPosition < barLength && barPosition > 0) {
				final String b1 = bar.substring(0, barPosition);
				final String b2 = bar.substring(barPosition);
				final String icon;
				if(bouncePositive) {
					icon = ICON_POS;
					barPosition++;
				} else {
					icon = ICON_NEG;
					barPosition--;
				}
				System.out.write((" " + b1 + icon + b2 + " " + 
						generateTruncatedMessage(msg, progressBarMsgLength) + "\r").getBytes());
			}
			
			if (barPosition == barLength) {
				barPosition--;
				bouncePositive = false;
			} else if (barPosition == 0) {
				barPosition++;
				bouncePositive = true;
			}
		} catch (IOException e) { //do nothing
		}
	}

	@Override
	public void reset() {
		count = 0;
	}
	
}
