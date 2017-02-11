package se.de.hu_berlin.informatik.utils.experiments.evo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public interface EvoMutation<T,L> {

	/**
	 * Mutates the target object based on the given location.
	 * @param target
	 * the target object to mutate
	 * @param location
	 * the location at which to mutate the target object
	 * @return
	 * the mutated object
	 */
	public T applyTo(T target, L location);
	
	public int getIDofNextMutation(L location);
	
	public static class History implements List<Integer> {

		private final List<Integer> history;
		
		public History() {
			history = new ArrayList<>();
		}
		
		public History(Collection<? extends Integer> c) {
			history = new ArrayList<>(c);
		}
		
		public History(int capacity) {
			history = new ArrayList<>(capacity);
		}
		
		@Override
		public int hashCode() {
			int result = 17;
			result = 31 * result + this.size();
			Iterator<Integer> iterator = this.iterator();
			while (iterator.hasNext()) {
				result = 31 * result + iterator.next();
			}
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof History) {
				History o = (History) obj;
				//must have the same number of elements
				if (this.size() != o.size()) {
					return false;
				}
				Iterator<Integer> iterator1 = this.iterator();
				Iterator<Integer> iterator2 = o.iterator();
				while(iterator1.hasNext()) {
					if (iterator1.next() != iterator2.next()) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		}

		@Override
		public int size() {
			return history.size();
		}

		@Override
		public boolean isEmpty() {
			return history.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return history.contains(o);
		}

		@Override
		public Iterator<Integer> iterator() {
			return history.iterator();
		}

		@Override
		public Object[] toArray() {
			return history.toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return history.toArray(a);
		}

		@Override
		public boolean add(Integer e) {
			return history.add(e);
		}

		@Override
		public boolean remove(Object o) {
			return history.remove(o);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return history.containsAll(c);
		}

		@Override
		public boolean addAll(Collection<? extends Integer> c) {
			return history.addAll(c);
		}

		@Override
		public boolean addAll(int index, Collection<? extends Integer> c) {
			return history.addAll(index, c);
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			return history.removeAll(c);
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return history.retainAll(c);
		}

		@Override
		public void clear() {
			history.clear();
		}

		@Override
		public Integer get(int index) {
			return history.get(index);
		}

		@Override
		public Integer set(int index, Integer element) {
			return history.set(index, element);
		}

		@Override
		public void add(int index, Integer element) {
			history.add(index, element);
		}

		@Override
		public Integer remove(int index) {
			return history.remove(index);
		}

		@Override
		public int indexOf(Object o) {
			return history.indexOf(o);
		}

		@Override
		public int lastIndexOf(Object o) {
			return history.lastIndexOf(o);
		}

		@Override
		public ListIterator<Integer> listIterator() {
			return history.listIterator();
		}

		@Override
		public ListIterator<Integer> listIterator(int index) {
			return history.listIterator(index);
		}

		@Override
		public List<Integer> subList(int fromIndex, int toIndex) {
			return history.subList(fromIndex, toIndex);
		}
		
	}
	
}
