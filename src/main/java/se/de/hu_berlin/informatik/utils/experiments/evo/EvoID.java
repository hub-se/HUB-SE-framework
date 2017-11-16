package se.de.hu_berlin.informatik.utils.experiments.evo;

public class EvoID<T extends Comparable<T>> implements Comparable<EvoID<T>> {

	private final int primary;
	private final T secondary;
	
	public EvoID(int primary, T secondary) {
		super();
		this.primary = primary;
		this.secondary = secondary;
	}

	public int getPrimary() {
		return primary;
	}

	public T getSecondary() {
		return secondary;
	}

	@Override
	public int hashCode() {
		return 31 * (527 + primary) + secondary.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EvoID) {
			EvoID<?> o = (EvoID<?>) obj;
			//must have the same primary id
			if (this.getPrimary() != o.getPrimary()) {
				return false;
			}
			//must have the same secondary id
			if (!this.getSecondary().equals(o.getSecondary())) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(EvoID<T> o) {
		if (o == null) {
			throw new NullPointerException();
		}
		//first, order by primary ids
		if (this.getPrimary() == o.getPrimary()) {
			//if primary ids are identical, order by secondary ids
			return this.getSecondary().compareTo(o.getSecondary());
		} else if (this.getPrimary() < o.getPrimary()) {
			return -1;
		} else {
			return 1;
		}
	}
	
	
}
