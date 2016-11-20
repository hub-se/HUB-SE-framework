package se.de.hu_berlin.informatik.utils.miscellaneous;

/**
 * Simple pair implementation.
 * @author Simon
 *
 * @param <A>
 * the type of the first object
 * @param <B>
 * the type of the second object
 */
public class Pair<A,B> {

	private A first = null;
	private B second = null;
	
	public Pair(A first, B second) {
		super();
		this.first = first;
		this.second = second;
	}

	public A first() {
		assert first != null;
		return first;
	}

	public void setFirst(A first) {
		this.first = first;
	}

	public B second() {
		assert second != null;
		return second;
	}

	public void setSecond(B second) {
		this.second = second;
	}
	
	
}
