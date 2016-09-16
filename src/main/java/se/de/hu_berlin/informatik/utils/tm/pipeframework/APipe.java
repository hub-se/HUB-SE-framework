/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.pipeframework;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.threaded.DisruptorEventHandler;
import se.de.hu_berlin.informatik.utils.threaded.DisruptorProvider;
import se.de.hu_berlin.informatik.utils.tm.ITransmitter;
import se.de.hu_berlin.informatik.utils.tracking.ProgressTracker;
import se.de.hu_berlin.informatik.utils.tracking.Trackable;

/**
 * An abstract class that provides basic functionalities of a pipe
 * framework. Classes that extend this abstract class may have an input 
 * provider and an output provider, can be linked together such that one pipe
 * provides the input of another pipe. Each pipe executes its calculations
 * inside of a thread which allows parallel execution of tasks. After usage,
 * the pipes have to be manually shutdown, though. (At least the first pipe
 * has to be manually shutdown. All other linked pipes are automatically
 * shutdown in a chain reaction after the shutdown of the first pipe.
 * 
 * <br><br> For convenience, multiple (matching) pipes may be linked together 
 * like this:
 * 
 * <br><br> {@code pipe1.linkTo(pipe2).linkTo(pipe3).linkTo(...)...;}
 * 
 * <br><br> which will link the output of {@code pipe1} to the input of 
 * {@code pipe2} and then link the output of {@code pipe2} to the input of
 * {@code pipe3}, etc.
 * 
 * <br><br> After linking, any matching item submitted to the first pipe
 * will start the execution process. Non-matching items will abort the
 * application with an error message. Objects that equal {@code null} will
 * be neither processed nor submitted to the output pipe if processing
 * a regular input item produces a {@code null} object.
 * 
 * <br><br> In general, pipes should not be linked manually and should
 * preferably be linked together with a {@link PipeLinker} which provides
 * more general and more easy access methods.
 * 
 * @author Simon Heiden
 *
 * @param <A>
 * is the type of the input objects
 * @param <B>
 * is the type of the output objects
 * 
 * @see PipeLinker
 */
public abstract class APipe<A,B> extends Trackable implements ITransmitter<A,B> {
	
	final private DisruptorProvider<A> disruptorProvider;

	private boolean hasInput = false;
	private APipe<B,?> output = null;

	/**
	 * Creates a pipe object with a buffer size of 8.
	 */
	public APipe() {
		this(8);
	}
	
	/**
	 * Creates a pipe object.
	 * @param bufferSize
	 * the size of the ring buffer, must be power of 2
	 */
	@SuppressWarnings("unchecked")
	public APipe(int bufferSize) {
		super();
		disruptorProvider = new DisruptorProvider<>(bufferSize);
		disruptorProvider.connectHandlers(new PipeEventHandler());
	}
	
	/**
	 * Sets this pipe to have an input pipe. 
	 */
	protected void setInput() {
		hasInput = true;
	}
	
	/**
	 * @return
	 * whether an input pipe exists
	 */
	private boolean hasInput() {
		return hasInput;
	}

	/**
	 * @param pipe
	 * the output pipe
	 */
	private void setOutput(APipe<B,?> pipe) {
		output = pipe;
	}

	/**
	 * Submits an object of type {@code B} to a connected output pipe.
	 * If the pipe is not linked to any other pipe, then the item is 
	 * discarded.
	 * @param item
	 * the object of type {@code B} to be submitted
	 */
	public void submitProcessedItem(B item) {
		if (output != null) {
			output.submit(item);
		}
	}


	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#linkTo(se.de.hu_berlin.informatik.utils.tm.ITransmitter)
	 */
	public <C, D> ITransmitter<C, D> linkTo(ITransmitter<C, D> transmitter) {
		if (transmitter instanceof APipe) {
			return linkPipeTo((APipe<C, D>)transmitter);
		} else {
			Log.abort(this, "Can only link to other pipes.");
		}
		return null;
	}

	/**
	 * Links a matching pipe to the output of this pipe.
	 * @param <C>
	 * the input type of the pipe to be linked to
	 * @param <D>
	 * the output type of the pipe to be linked to
	 * @param pipe
	 * the pipe to be linked to
	 * @return
	 * the pipe to be linked to
	 */
	@SuppressWarnings("unchecked")
	private <C,D> APipe<C, D> linkPipeTo(APipe<C, D> pipe) {
		if (!pipe.hasInput()) {
			//output pipe has no input yet
			try {				
				setOutput((APipe<B, ?>) pipe);
				pipe.setInput();
			} catch (ClassCastException e) {
				Log.abort(this, e, "Type mismatch while linking to %s.", pipe.toString());
			}
		} else {
			Log.abort(this, "No linking to already used pipes allowed!");
		}
		return pipe;
	}

	/**
	 * Shuts down the pipe. Waits for all executions to terminate.
	 */
	public void shutdown() {
//		Log.out(this, "waiting for pending items...");
		disruptorProvider.waitForPendingEventsToFinish();
		
		//check whether there are collected items to submit
		B result;
		if ((result = getResultFromCollectedItems()) != null) {
			//submit the collected result
			track();
			submitProcessedItem(result);
		}
		
//		Log.out(this, "Shutting down...");
		//shut down the disruptor
		disruptorProvider.shutdown();
		//initiate shut down of the pipe linked to this pipe's output (if any)
		if (output != null) {
			output.shutdown();
		}
	}

	/**
	 * Submits an item to this pipe.
	 * @param item
	 * the item to be submitted
	 */
	public void submit(A item) {
		if (item != null) {
			disruptorProvider.submit(item);
		}
	}
	
	/**
	 * Submits an item of some kind to this pipe. Will abort the
	 * application if the type does not match the pipe's input type.
	 * More specificially, it will abort if the item can't be cast
	 * to the pipe's input type.
	 * @param item
	 * the item to be submitted
	 */
	@SuppressWarnings("unchecked")
	public void submitObject(Object item) {
		try {
			submit((A)item);
		} catch (ClassCastException e) {
			Log.abort(this, e, "Type mismatch while submitting item.");
		}
	}

	@Override
	public APipe<A,B> enableTracking() {
		super.enableTracking();
		return this;
	}

	@Override
	public APipe<A,B> enableTracking(int stepWidth) {
		super.enableTracking(stepWidth);
		return this;
	}

	@Override
	public APipe<A,B> disableTracking() {
		super.disableTracking();
		return this;
	}

	@Override
	public APipe<A,B> enableTracking(ProgressTracker tracker) {
		super.enableTracking(tracker);
		return this;
	}

	@Override
	public APipe<A,B> enableTracking(boolean useProgressBar) {
		super.enableTracking(useProgressBar);
		return this;
	}

	@Override
	public APipe<A,B> enableTracking(boolean useProgressBar, int stepWidth) {
		super.enableTracking(useProgressBar, stepWidth);
		return this;
	}

	public class PipeEventHandler extends DisruptorEventHandler<A> {

		@Override
		public void processEvent(A input) throws Exception {
			track();
			submitProcessedItem(processItem(input));
		}

	}

}
