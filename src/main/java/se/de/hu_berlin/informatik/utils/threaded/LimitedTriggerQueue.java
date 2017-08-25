/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threaded;

import java.util.concurrent.TimeUnit;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;

/**
 * Implementation of a blocking queue to be used by, for example, a
 * {@link java.util.concurrent.ExecutorService}.
 * 
 * @author Simon Heiden
 */
public class LimitedTriggerQueue<E, T> extends LimitedQueue<E> {

	private static final long serialVersionUID = -1182628988321582636L;

	private T trigger; 
	
	private E offerAfterTrigger;
	
	private boolean wasTriggered = false;

	/**
	 * Creates a {@link LimitedTriggerQueue} with the given maximum size.
	 * @param maxSize
	 * is the maximum size of the queue.
	 * @param trigger
	 * a dummy trigger object
	 * @param offerAfterTrigger
	 * an element to submit to the queue after the trigger element has been
	 * submitted and after the queue is emptied (to ensure that it is inserted 
	 * after all other items have been submitted) 
	 */
	public LimitedTriggerQueue(int maxSize, T trigger, E offerAfterTrigger) {
		super(maxSize);
		this.trigger = trigger;
		this.offerAfterTrigger = offerAfterTrigger;
	}
	
	/**
	 * Creates a {@link LimitedTriggerQueue} with the given maximum size.
	 * @param maxSize
	 * is the maximum size of the queue.
	 * @param trigger
	 * a dummy trigger object
	 */
	public LimitedTriggerQueue(int maxSize, T trigger) {
		this(maxSize, trigger, null);
	}
	
	public void setItemToOfferAfterTrigger(E offerAfterTrigger) {
		this.offerAfterTrigger = offerAfterTrigger;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.concurrent.LinkedBlockingQueue#offer(java.lang.Object)
	 */
	@Override
	public boolean offer(E e) {
		if (e.equals(trigger)) {
			if (offerAfterTrigger != null) {
				wasTriggered = true;
			}
			return offerIfEmpty();
		} else {
			return super.offer(e);
		}
	}

	private boolean offerIfEmpty() {
		Log.out(this, "offering1");
		boolean done = false;
		while (!done) {
			if (this.isEmpty()) {
				Log.out(this, "offering");
				done = super.offer(offerAfterTrigger);
			} else {
				done = true;
			}
		}
		return done;
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


}
