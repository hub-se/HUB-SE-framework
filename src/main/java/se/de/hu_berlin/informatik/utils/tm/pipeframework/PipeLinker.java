/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.pipeframework;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.optionparser.OptionCarrier;
import se.de.hu_berlin.informatik.utils.optionparser.OptionParser;
import se.de.hu_berlin.informatik.utils.tm.user.ProcessorSocketGenerator;
import se.de.hu_berlin.informatik.utils.tracking.Trackable;
import se.de.hu_berlin.informatik.utils.tracking.TrackingStrategy;
import se.de.hu_berlin.informatik.utils.tracking.TrackerDummy;

/**
 * Provides more general and easy access methods for the linking of Pipes
 * and for the submission of items to a chain of Pipes.
 * 
 * @author Simon Heiden
 *
 */
public class PipeLinker implements Trackable, OptionCarrier {
	
	/**
	 * Creates a new pipe linker. Assumes that input items are
	 * submitted from a single thread. If multiple threads submit
	 * items to this linker, use the other constructor and set
	 * the parameter to false.
	 */
	public PipeLinker() {
		this(true);
	}

	/**
	 * Creates a new pipe linker.
	 * @param singleWriter
	 * whether input items are submitted from a single thread
	 */
	public PipeLinker(boolean singleWriter) {
		super();
		this.singleWriter = singleWriter;
	}
	
	/**
	 * Creates a new pipe linker. Assumes that input items are
	 * submitted from a single thread. If multiple threads submit
	 * items to this linker, use the other constructor and set
	 * the parameter to false.
	 * @param options
	 * an options object to distribute to the pipes in this linker
	 */
	public PipeLinker(OptionParser options) {
		this(true, options);
	}

	/**
	 * Creates a new pipe linker.
	 * @param singleWriter
	 * whether input items are submitted from a single thread
	 * @param options
	 * an options object to distribute to the pipes in this linker
	 */
	public PipeLinker(boolean singleWriter, OptionParser options) {
		this(singleWriter);
		this.options = options;
	}

	private boolean singleWriter = true;
	private Pipe<?,?> startPipe = null;
	private Pipe<?,?> endPipe = null;
	private OptionParser options;

	/**
	 * Links the given Pipes (provided by socket generators, possibly) 
	 * together and appends them to former appended Pipes, if any. 
	 * If the Pipes don't match, then execution stops and the application aborts.
	 * @param generators
	 * Pipes to be linked together (given as generators, possibly)
	 * @return
	 * this PipeLinker
	 */
	public PipeLinker append(ProcessorSocketGenerator<?,?>... generators) {	
		if (generators.length != 0) {
			try {
				generators[0].asPipe().setOptions(options);
				if (startPipe == null) {
					startPipe = generators[0].asPipe();
					//set whether input items are submitted with a single thread
					startPipe.setProducerType(singleWriter);
					if (isTracking()) {
						startPipe.enableTracking(getTracker());
					}
				} else {
					endPipe.linkTo(generators[0].asPipe());
				}

				for (int i = 0; i < generators.length-1; ++i) {
					generators[i].asPipe().linkTo(generators[i+1].asPipe());
					generators[i+1].asPipe().setOptions(options);
				}

				endPipe = generators[generators.length-1].asPipe();
			} catch(UnsupportedOperationException e) {
				Log.abort(this, e, "Unable to get pipe from a given transmitter.");
			}
		}
		return this;
	}
	
	/**
	 * Retrieves the start pipe or aborts the application if none set.
	 * @return
	 * the start pipe
	 */
	private Pipe<?, ?> getStartPipe() {
		if (startPipe == null) {
			Log.abort(this, "No start pipe available.");
		}
		return startPipe;
	}

	/**
	 * Submits a single or multiple items to the underlying chain
	 * of pipes.
	 * @param items
	 * items to be submitted
	 * @return
	 * this pipe linker
	 */
	public PipeLinker submit(Object... items) {
		for (int i = 0; i < items.length; ++i) {
			getStartPipe().submitObject(items[i]);
		}
		return this;
	}
	
	/**
	 * Shuts down the pipe chain. Has to be called to complete execution.
	 * Otherwise, the application won't stop. Will return when the pipe
	 * chain has completed all executions.
	 */
	public void shutdown() {
		getStartPipe().shutdown();
	}
	
	/**
	 * Submits a single or multiple items to the underlying chain
	 * of pipes and shuts down the pipe afterwards for convenience.
	 * @param items
	 * items to be submitted
	 */
	public void submitAndShutdown(Object... items) {
		for (int i = 0; i < items.length; ++i) {
			getStartPipe().submitObject(items[i]);
		}
		shutdown();
	}
	
	@Override
	public PipeLinker enableTracking() {
		if (startPipe != null) {
			startPipe.enableTracking();
		}
		return this;
	}
	
	@Override
	public PipeLinker enableTracking(int stepWidth) {
		if (startPipe != null) {
			startPipe.enableTracking(stepWidth);
		}
		return this;
	}

	@Override
	public PipeLinker disableTracking() {
		if (startPipe != null) {
			startPipe.disableTracking();
		}
		return this;
	}

	@Override
	public PipeLinker enableTracking(TrackingStrategy tracker) {
		if (startPipe != null) {
			startPipe.enableTracking(tracker);
		}
		return this;
	}

	@Override
	public PipeLinker enableTracking(boolean useProgressBar) {
		if (startPipe != null) {
			startPipe.enableTracking(useProgressBar);
		}
		return this;
	}

	@Override
	public PipeLinker enableTracking(boolean useProgressBar, int stepWidth) {
		if (startPipe != null) {
			startPipe.enableTracking(useProgressBar, stepWidth);
		}
		return this;
	}

	@Override
	public boolean isTracking() {
		if (startPipe != null) {
			return startPipe.isTracking();
		}
		return false;
	}

	@Override
	public void track() {
		if (startPipe != null) {
			startPipe.track();
		}
	}

	@Override
	public void track(String msg) {
		if (startPipe != null) {
			startPipe.track(msg);
		}
	}

	@Override
	public void delegateTrackingTo(Trackable target) {
		if (startPipe != null) {
			startPipe.delegateTrackingTo(target);
		}
	}

	@Override
	public TrackingStrategy getTracker() {
		if (startPipe != null) {
			return startPipe.getTracker();
		}
		return TrackerDummy.getInstance();
	}

	@Override
	public void setTracker(TrackingStrategy tracker) {
		if (startPipe != null) {
			startPipe.setTracker(tracker);
		}
	}
	
	@Override
	public boolean onlyForced() {
		if (startPipe != null) {
			return startPipe.onlyForced();
		} else {
			return false;
		}
	}

	@Override
	public void allowOnlyForcedTracks() {
		if (startPipe != null) {
			startPipe.allowOnlyForcedTracks();
		}
	}

	@Override
	public OptionParser getOptions() {
		return options;
	}

	@Override
	public PipeLinker setOptions(OptionParser options) {
		this.options = options;
		return this;
	}

	@Override
	public boolean hasOptions() {
		return options != null;
	}
	
}
