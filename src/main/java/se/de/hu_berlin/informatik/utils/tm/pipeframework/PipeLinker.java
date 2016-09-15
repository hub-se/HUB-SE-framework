/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.pipeframework;

import java.util.ArrayList;
import java.util.List;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.tm.ITransmitter;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AModule;
import se.de.hu_berlin.informatik.utils.tm.pipes.ModuleLoaderPipe;
import se.de.hu_berlin.informatik.utils.tracking.ProgressTracker;
import se.de.hu_berlin.informatik.utils.tracking.Trackable;

/**
 * Provides more general and easy access methods for the linking of pipes
 * and for the submission of items to a chain of pipes.
 * 
 * @author Simon Heiden
 *
 */
public class PipeLinker extends Trackable {

	private APipe<?,?> startPipe = null;

	/**
	 * Links the given transmitters together to a chain of pipes. 
	 * If the transmitters don't match, then
	 * execution stops and the application aborts.
	 * @param transmitters
	 * transmitters (pipes or modules) to be linked together
	 * @return
	 * this linker
	 */
	public PipeLinker link(ITransmitter<?,?>... transmitters) {	
		if (transmitters.length != 0) {
			List<APipe<?,?>> pipes = new ArrayList<>(transmitters.length);
			for (int i = 0; i < transmitters.length; ++i) {
				pipes.add(getPipeFromTransmitter(transmitters[i]));
			}

			startPipe = pipes.get(0);

			for (int i = 0; i < pipes.size()-1; ++i) {
				pipes.get(i).linkTo(pipes.get(i+1));
			}
			
			if (isTracking()) {
				startPipe.enableTracking(getTracker());
			}
		}
		return this;
	}
	
	/**
	 * Converts a transmitter to a pipe. If the transmitter is a pipe, then
	 * it is simply returned. If it is a module, then it is adapted via a
	 * {@link ModuleLoaderPipe}.
	 * @param transmitter
	 * the transmitter to be converted to a pipe
	 * @return
	 * the converted transmitter
	 */
	private <A,B> APipe<A,B> getPipeFromTransmitter(ITransmitter<A,B> transmitter) {
		if (transmitter instanceof APipe) {
			return (APipe<A,B>)transmitter;
		} else if (transmitter instanceof AModule) {
			return new ModuleLoaderPipe<A,B>((AModule<A,B>)transmitter);
		} else {
			Log.abort(this, "Unable to obtain type of transmitter %s.", transmitter.toString());
		}
		return null;
	}
	
	/**
	 * Retrieves the start pipe or aborts the application if none set.
	 * @return
	 * the start pipe
	 */
	private APipe<?, ?> getStartPipe() {
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
		super.enableTracking();
		if (startPipe != null) {
			startPipe.enableTracking(this.getTracker());
		}
		return this;
	}
	
	@Override
	public PipeLinker enableTracking(int stepWidth) {
		super.enableTracking(stepWidth);
		if (startPipe != null) {
			startPipe.enableTracking(this.getTracker());
		}
		return this;
	}

	@Override
	public PipeLinker disableTracking() {
		super.disableTracking();
		if (startPipe != null) {
			startPipe.disableTracking();
		}
		return this;
	}

	@Override
	public PipeLinker enableTracking(ProgressTracker tracker) {
		super.enableTracking(tracker);
		if (startPipe != null) {
			startPipe.enableTracking(this.getTracker());
		}
		return this;
	}
	
}
