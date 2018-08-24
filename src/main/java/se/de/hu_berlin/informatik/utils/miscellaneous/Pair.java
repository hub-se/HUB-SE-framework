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
	
	public Pair() {
		super();
	}
	
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
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pair) {
			Pair<?,?> other = (Pair<?,?>) obj;
			if (this.first != null && this.second != null) {
				if (!this.first.equals(other.first) || !this.second.equals(other.second)) {
					return false;
				}
				return true;
			} else if (this.first != null) {
				if (!this.first.equals(other.first) || other.second != null) {
					return false;
				}
				return true;
			} else if (this.second != null) {
				if (other.first != null || !this.second.equals(other.second)) {
					return false;
				}
				return true;
			} else {
				if (other.first != null || other.second != null) {
					return false;
				}
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash1 = first == null ? 0 : first.hashCode();
		int hash2 = second == null ? 0 : second.hashCode();
		return 31 * (527 + hash1) + hash2;
	}
}
