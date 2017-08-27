/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threaded;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of a blocking queue to be used by, for example, a
 * {@link java.util.concurrent.ExecutorService}.
 * 
 * @author Simon Heiden
 */
public class LimitedTriggerQueue<E, T> extends LimitedQueue<E> {

	private static final long serialVersionUID = -1182628988321582636L;

	private E offerAfterTrigger;
	
	private boolean wasTriggered = false;
	private boolean wasSubmitted = false;

	/**
	 * Creates a {@link LimitedTriggerQueue} with the given maximum size.
	 * @param maxSize
	 * is the maximum size of the queue.
	 * @param offerAfterTrigger
	 * an element to submit to the queue after the trigger method has been
	 * executed and after the queue is emptied (to ensure that it is inserted 
	 * after all other items have been submitted) 
	 */
	public LimitedTriggerQueue(int maxSize, E offerAfterTrigger) {
		super(maxSize);
		this.offerAfterTrigger = offerAfterTrigger;
	}
	
	/**
	 * Creates a {@link LimitedTriggerQueue} with the given maximum size.
	 * @param maxSize
	 * is the maximum size of the queue.
	 */
	public LimitedTriggerQueue(int maxSize) {
		this(maxSize, null);
	}
	
	public void setItemToOfferAfterTrigger(E offerAfterTrigger) {
		this.offerAfterTrigger = offerAfterTrigger;
	}
	
	public void trigger() {
		if (offerAfterTrigger != null) {
			wasTriggered = true;
			offerIfEmpty();
		}
	}

	private void offerIfEmpty() {
		if (!wasSubmitted) {
			boolean done = false;
			while (!done) {
				if (this.isEmpty()) {
					done = super.offer(offerAfterTrigger);
					wasSubmitted = done;
				} else {
					done = true;
				}
			}
		}
	}

	@Override
	public E take() throws InterruptedException {
		E take = super.take();
		if (wasTriggered) {
			offerIfEmpty();
		}
		return take;
	}

	@Override
	public E poll() {
		E poll = super.poll();
		if (wasTriggered) {
			offerIfEmpty();
		}
		return poll;
	}

	@Override
	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		E poll = super.poll(timeout, unit);
		if (wasTriggered) {
			offerIfEmpty();
		}
		return poll;
	}

	@Override
	public E remove() {
		E poll = super.remove();
		if (wasTriggered) {
			offerIfEmpty();
		}
		return poll;
	}

	@Override
	public boolean remove(Object o) {
		boolean remove = super.remove(o);
		if (wasTriggered) {
			offerIfEmpty();
		}
		return remove;
	}

	@Override
	public int drainTo(Collection<? super E> c) {
		int drainTo = super.drainTo(c);
		if (wasTriggered) {
			offerIfEmpty();
		}
		return drainTo;
	}

	@Override
	public int drainTo(Collection<? super E> c, int maxElements) {
		int drainTo = super.drainTo(c, maxElements);
		if (wasTriggered) {
			offerIfEmpty();
		}
		return drainTo;
	}


}
