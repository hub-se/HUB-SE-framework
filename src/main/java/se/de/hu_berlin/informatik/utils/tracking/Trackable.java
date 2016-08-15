package se.de.hu_berlin.informatik.utils.tracking;

public abstract class Trackable {

	private ProgressTracker tracker = null;
	private boolean isTracking = false;

	/**
	 * Enables tracking of progress. Doesn't use a progress bar.
	 * @return
	 * this object for chaining
	 */
	public Trackable enableTracking() {
		return enableTracking(false);
	}
	
	/**
	 * Enables tracking of progress.
	 * @param useProgressBar
	 * whether to use a progress bar to show the progress
	 * @return
	 * this object for chaining
	 */
	public Trackable enableTracking(boolean useProgressBar) {
		if (tracker == null) {
			tracker = new ProgressTracker(useProgressBar);
		}
		isTracking = true;
		return this;
	}
	
	/**
	 * Enables tracking of progress. Uses the given
	 * step width to only produce output after the
	 * given number of tracked elements. Doesn't use 
	 * a progress bar.
	 * @param stepWidth
	 * sets the step width for producing outputs
	 * @return
	 * this object for chaining
	 */
	public Trackable enableTracking(int stepWidth) {
		return enableTracking(false, stepWidth);
	}
	
	/**
	 * Enables tracking of progress. Uses the given
	 * step width to only produce output after the
	 * given number of tracked elements.
	 * @param useProgressBar
	 * whether to use a progress bar to show the progress
	 * @param stepWidth
	 * sets the step width for producing outputs
	 * @return
	 * this object for chaining
	 */
	public Trackable enableTracking(boolean useProgressBar, int stepWidth) {
		if (tracker == null) {
			tracker = new ProgressTracker(useProgressBar, stepWidth);
		}
		isTracking = true;
		return this;
	}
	
	/**
	 * Disables tracking of progress.
	 * @return
	 * this object for chaining
	 */
	public Trackable disableTracking() {
		isTracking = false;
		return this;
	}
	
	/**
	 * Enables tracking of progress, while using the given 
	 * tracker object. If the given tracker is null, then a
	 * new tracker is created that doesn't use a progress bar.
	 * @param tracker
	 * a tracker object to use
	 * @return
	 * this object for chaining
	 */
	public Trackable enableTracking(ProgressTracker tracker) {
		if (tracker != null) {
			this.tracker = tracker;
		} else {
			this.tracker = new ProgressTracker(false);
		}
		isTracking = true;
		return this;
	}
	
	/**
	 * @return
	 * whether this transmitter's progress tracking is enabled
	 */
	public boolean isTracking() {
		return isTracking;
	}
	
	/**
	 * @return
	 * the tracker object, or null if none exists
	 */
	public ProgressTracker getTracker() {
		return tracker;
	}
	
	/**
	 * Tracks the progress for a processed element if tracking
	 * has been enabled.
	 */
	public void track() {
		if (isTracking) {
			tracker.track();
		}
	}
	
	/**
	 * Tracks the progress for a processed element if tracking
	 * has been enabled.
	 * @param msg
	 * a message to display
	 */
	public void track(String msg) {
		if (isTracking) {
			tracker.track(msg);
		}
	}
	
	/**
	 * Delegates the tracking tasks from this object to the given
	 * target trackable object.
	 * @param target
	 * the Trackable object to delegate the tracking to
	 */
	public void delegateTrackingTo(Trackable target) {
		if (this.isTracking()) {
			target.enableTracking(this.getTracker());
			this.disableTracking();
		}
	}
	
}
