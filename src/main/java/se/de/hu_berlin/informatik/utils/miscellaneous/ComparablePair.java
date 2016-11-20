package se.de.hu_berlin.informatik.utils.miscellaneous;

/**
 * Simple pair implementation with ordering based on the first elements.
 * @author Simon
 *
 * @param <A>
 * the type of the first object
 * @param <B>
 * the type of the second object
 */
public class ComparablePair<A extends Comparable<A>,B> extends Pair<A, B> implements Comparable<ComparablePair<A, B>> {

	public ComparablePair(A first, B second) {
		super(first, second);
	}

	@Override
	public int compareTo(ComparablePair<A, B> o) {
		return this.first().compareTo(o.first());
	}
	
	
}
