package se.de.hu_berlin.informatik.utils.miscellaneous;

/**
 * Simple triple implementation.
 * @author Simon
 *
 * @param <A>
 * the type of the first object
 * @param <B>
 * the type of the second object
 * @param <C>
 * the type of the third object
 */
public class Triple<A,B,C> {

	private A first = null;
	private B second = null;
	private C third = null;
	
	public Triple(A first, B second, C third) {
		super();
		this.first = first;
		this.second = second;
		this.third = third;
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
	
	public C third() {
		assert third != null;
		return third;
	}

	public void setThird(C third) {
		this.third = third;
	}
	
}
