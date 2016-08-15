package se.de.hu_berlin.informatik.utils.tracking;

/**
 * Class to track the progress of processes.
 * 
 * @author Simon
 */
public class ProgressTracker {
	
	ITrackingStrategy tracker;
	
	public ProgressTracker(boolean useProgressBar) {
		if (useProgressBar) {
			tracker = new ProgressBarTracker(1);
		} else {
			tracker = new SimpleTracker();
		}
	}
	
	public ProgressTracker(boolean useProgressBar, int stepWidth) {
		assert stepWidth > 0;
		if (useProgressBar) {
			tracker = new ProgressBarTracker(stepWidth);
		} else {
			tracker = new StepWiseTracker(stepWidth);
		}
	}

	public void track() {
		tracker.track();
	}
	
	public void track(String msg) {
		tracker.track(msg);
	}
	
}
