package se.de.hu_berlin.informatik.utils.tracking;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;

public class NewProgressBarTracker implements TrackingStrategy {

	private int stepWidth = 0;
	private int count = 0;
	
	private ProgressBar pb;
	
	public NewProgressBarTracker(final int stepWidth, int max) {
		super();
		this.stepWidth = stepWidth;
		//pb = new ProgressBar("", max); // name, initial max
		pb = new ProgressBar("", max, ProgressBarStyle.ASCII); // if you want ASCII output style
		pb.start(); // the progress bar starts timing
	}

	@Override
	public void track() {
		if (++count % stepWidth == 0 || count == 1) {
			pb.stepBy(stepWidth); // step by n
		}
	}
	
	@Override
	public void track(final String msg) {
		pb.setExtraMessage(msg);
		if (++count % stepWidth == 0 || count == 1) {
			pb.stepBy(stepWidth); // step by n
		}
	}

	@Override
	public void reset() {
		pb.stop();
		count = 0;
		pb.start();
	}

	@Override
	protected void finalize() throws Throwable {
		pb.stop();
		super.finalize();
	}

}
