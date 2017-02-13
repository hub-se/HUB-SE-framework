package se.de.hu_berlin.informatik.utils.experiments.evo;

public class EvoID implements Comparable<EvoID> {

	private final int primary;
	private final int secondary;
	
	public EvoID(int primary, int secondary) {
		super();
		this.primary = primary;
		this.secondary = secondary;
	}

	public int getPrimary() {
		return primary;
	}

	public int getSecondary() {
		return secondary;
	}

	@Override
	public int hashCode() {
		return 31 * (527 + primary) + secondary;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EvoID) {
			EvoID o = (EvoID) obj;
			//must have the same primary id
			if (this.getPrimary() != o.getPrimary()) {
				return false;
			}
			//must have the same secondary id
			if (this.getSecondary() != o.getSecondary()) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(EvoID o) {
		if (o == null) {
			throw new NullPointerException();
		}
		//first, order by primary ids
		if (this.getPrimary() == o.getPrimary()) {
			//if primary ids are identical, order by secondary ids
			if (this.getSecondary() < o.getSecondary()) {
				return -1;
			} else if (this.getSecondary() > o.getSecondary()) {
				return 1;
			} else {
				return 0;
			}
		} else if (this.getPrimary() < o.getPrimary()) {
			return -1;
		} else {
			return 1;
		}
	}
	
	
}
