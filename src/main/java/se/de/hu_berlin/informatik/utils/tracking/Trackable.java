package se.de.hu_berlin.informatik.utils.tracking;

public abstract class Trackable {

	private ProgressTracker tracker = null;
	private boolean isTracking = false;

	/**
	 * Enables tracking of progress.
	 * @return
	 * this object for chaining
	 */
	public Trackable enableTracking() {
		if (tracker == null) {
			tracker = new ProgressTracker();
		}
		isTracking = true;
		return this;
	}
	
	/**
	 * Enables tracking of progress. Uses the given
	 * step width to only produce output after the
	 * given number of tracked elements.
	 * @param stepWidth
	 * sets the step width for producing outputs
	 * @return
	 * this object for chaining
	 */
	public Trackable enableTracking(int stepWidth) {
		if (tracker == null) {
			tracker = new ProgressTracker(stepWidth);
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
	 * Enables tracking of progress.
	 * @param tracker
	 * a tracker object to use
	 * @return
	 * this object for chaining
	 */
	public Trackable enableTracking(ProgressTracker tracker) {
		if (tracker != null) {
			this.tracker = tracker;
		} else {
			this.tracker = new ProgressTracker();
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
