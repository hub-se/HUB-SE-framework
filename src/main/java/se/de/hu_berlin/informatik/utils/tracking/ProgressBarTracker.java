package se.de.hu_berlin.informatik.utils.tracking;

import java.io.IOException;

public class ProgressBarTracker implements ITrackingStrategy {

	final private static String ICON_POS = ">";
	final private static String ICON_NEG = "<";
	
	final private static char BAR_OPEN = '[';
	final private static char BAR_CLOSE = ']';
	final private static char BAR_MIDDLE = '-';
	
	private String bar = "[--------------------]";
	private int barLength = bar.length();
	
	private int progress_bar_msg_length = 80 - barLength - 4;
	
	private boolean bouncePositive = true;
	private int barPosition = 1;
	
	private int stepWidth = 0;
	private int count = 0;
	
	public ProgressBarTracker(int stepWidth) {
		super();
		this.stepWidth = stepWidth;
	}
	
	public ProgressBarTracker(int stepWidth, int barLength) {
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
		progress_bar_msg_length = 80 - barLength - 4;
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
			if (barPosition < barLength && barPosition > 0) {
				String b1 = bar.substring(0, barPosition);
				String b2 = bar.substring(barPosition);
				String icon;
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
	
	private void updateAndWriteTrackBar(String msg) {
		try {
			if (barPosition < barLength && barPosition > 0) {
				String b1 = bar.substring(0, barPosition);
				String b2 = bar.substring(barPosition);
				String icon;
				if(bouncePositive) {
					icon = ICON_POS;
					barPosition++;
				} else {
					icon = ICON_NEG;
					barPosition--;
				}
				System.out.write((" " + b1 + icon + b2 + " " + 
						generateTruncatedMessage(msg, progress_bar_msg_length) + "\r").getBytes());
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
