package se.de.hu_berlin.informatik.utils.experiments.evo;

public class EvoID {

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
		int hashCode = 17;
		hashCode = 31 * hashCode + primary;
		hashCode = 31 * hashCode + secondary;
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EvoID) {
			EvoID o = (EvoID) obj;
			//must have the same primary id
			if (this.getPrimary() != o.getPrimary()) {
				return false;
			}
			//must have the same scondary id
			if (this.getSecondary() != o.getSecondary()) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}
	
	
}
