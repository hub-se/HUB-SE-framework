/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.pipeframework;

import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;
import se.de.hu_berlin.informatik.utils.tm.ITransmitter;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.PipeLinker;

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
public abstract class APipe<A,B> implements ITransmitter<A,B>, Runnable {

	private Thread thread = null;
	private IProvider<A,APipe<?,?>> input = null;
	private IProvider<B,APipe<?,?>> output = null;
	
	private boolean isLinked = false;
	
	/**
	 * Creates a pipe object with a default output provider size of 5.
	 */
	public APipe() {
		this(5);
	}
	
	/**
	 * Creates a pipe object with the given output provider size.
	 * @param pipeSize
	 * the size of the output provider
	 */
	public APipe(int pipeSize) {
		super();
		output = new SynchronizedProvider<>(pipeSize);
	}

	/**
	 * @return
	 * the {@link Thread} in which the implementing object is running. 
	 */
	private Thread getThread() {
		return thread;
	}

	/**
	 * @param thread
	 * the running thread
	 */
	private void setThread(Thread thread) {
		this.thread = thread; 
	}

	/**
	 * @return
	 * if an input provider exists
	 */
	private boolean hasInput() {
		return (input != null);
	}

	/**
	 * @return
	 * the input provider
	 */
	private IProvider<A,APipe<?,?>> getInput() {
		return input;
	}

	/**
	 * @param provider
	 * the input provider
	 */
	protected void setInput(IProvider<A,APipe<?,?>> provider) {
		input = provider;
	}

	/**
	 * @return
	 * the output provider
	 */
	private IProvider<B,APipe<?,?>> getOutput() {
		return output;
	}
	
	/**
	 * Starts the automatic processing loop. Notifies the
	 * output provider about starting execution.
	 * @return
	 * the running thread
	 */
	protected Thread startAutomaticProcessing() {
		if (getThread() == null || !getThread().isAlive()) {
			Thread thread = new Thread(this);
//			System.out.println(thread.getName() + " started");
			getOutput().setProviderWorking();
			thread.start();
			return thread;
		}
		return getThread();
	}
	
	/**
	 * Submits an object of type {@code B} to a connected output pipe.
	 * If the pipe is not linked to any other pipe, then the item is 
	 * discarded to avoid deadlocks where the output provider queue
	 * is full and blocks further submission of items.
	 * @param item
	 * the object of type {@code B} to be submitted
	 */
	public void submitProcessedItem(B item) {
		if (isLinked()) {
			output.submit(item);
		}
	}
	
	/**
	 * @return
	 * if the pipe is linked to another pipe
	 */
	private boolean isLinked() {
		return isLinked ;
	}
	
	/**
	 * This method is executed repeatedly until the necessary conditions 
	 * for a thread shutdown are met. Tries to obtain an item from the input
	 * provider, processes it and submits the processed item to the output
	 * provider. Obtained input items that are null are ignored and not
	 * processed.
	 * @return
	 * true if the operation succeeded and false otherwise
	 */
	private boolean tryProcessItem() {
		A item;
		if ((item = tryToGetNextInputItem()) != null) {
			B result;
			if ((result = processItem(item)) != null) {
				submitProcessedItem(result);
				return true;
			}
		}
		return false;
	}

	/**
	 * Attempts to obtain an item from a connected input.
	 * @return
	 * object of type A or null if operation did not succeed
	 */
	private A tryToGetNextInputItem() {
		if (hasInput()) {
			A item;
			if ((item = getInput().get()) != null) {
				return item;
			}
		} else {
			Misc.abort(this, "Started thread with no input.");
		}
		return null;
	}

	/**
	 * Starts a loop that repeatedly tries to process an item and checks if the shutdown
	 * conditions are met. If the shutdown conditions are met, then the loop is broken. 
	 */
	public void run() {
		while (true) {
			tryProcessItem();
			if (shutdownConditionsFulfilled()) {
				//we are done, so set the input to null
				input = null;
				//and set the link state to false
				isLinked = false;
				//now we can reuse the pipe
				break;
			}
		}
	}
	
	/**
	 * This method tests the necessary
	 * conditions that have to be met to be able to shut down the pipe.
	 * If the pipe is about to get shut down, submit possibly pending
	 * results from collected items.
	 * @return
	 * true if the object should be shut down, false otherwise
	 */
	private boolean shutdownConditionsFulfilled() {
		if (!hasInput() || hasInput() && getInput().isProviderDone()) {
			B result;
			if ((result = getResultFromCollectedItems()) != null) {
				submitProcessedItem(result);
			}
			getOutput().setProviderDone();
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#linkTo(se.de.hu_berlin.informatik.utils.tm.ITransmitter)
	 */
	public <C, D> ITransmitter<C, D> linkTo(ITransmitter<C, D> transmitter) {
		if (transmitter instanceof APipe) {
			return linkPipeTo((APipe<C, D>)transmitter);
		} else {
			Misc.abort(this, "Can only link to other pipes.");
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
				pipe.setInput((IProvider<C,APipe<?,?>>)getOutput());
			} catch (ClassCastException e) {
				Misc.abort(this, e, "Type mismatch while linking to %s.", pipe.toString());
			}
			getOutput().setProviderWorking();
			pipe.setThread(pipe.startAutomaticProcessing());
			isLinked = true;
		} else {
			Misc.abort(this, "No linking to already used pipes allowed!");
		}
		return pipe;
	}
	
	/**
	 * Shuts down the pipe.
	 */
	public void shutdown() {
		createInputProviderIfNoneExists();
		getInput().setProviderDone();
	}
	
	/**
	 * Submits an item to this pipe.
	 * @param item
	 * the item to be submitted
	 * @return
	 * true if the operation succeeded, false otherwise
	 */
	public boolean submitItem(Object item) {
		createInputProviderIfNoneExists();
		
		getInput().submit(item);
		
		return true;
	}
	
	/**
	 * Initializes an input provider if none exists and starts the automatic
	 * processing procedure of this pipe.
	 */
	private void createInputProviderIfNoneExists() {
		if (!hasInput()) {
			input = new SynchronizedProvider<A>(5);
			setThread(startAutomaticProcessing());
		}
	}
	
	/**
	 * Waits for the complete shutdown of this pipe.
	 */
	public void waitForShutdown() {
		getOutput().waitForShutdown();
	}
	
}
