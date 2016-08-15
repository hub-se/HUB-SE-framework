package se.de.hu_berlin.informatik.utils.tracking;

public class StepWiseTracker implements ITrackingStrategy {

	private int stepWidth = 0;
	private int count = 0;
	
	public StepWiseTracker(int stepWidth) {
		super();
		this.stepWidth = stepWidth;
	}

	@Override
	public void track() {
		if (++count % stepWidth == 0 || count == 1) {
			writeTrackMsg(count);
		}
	}

	@Override
	public void track(String msg) {
		if (++count % stepWidth == 0 || count == 1) {
			writeTrackMsg(count, msg);
		}
	}

}
