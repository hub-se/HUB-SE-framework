package se.de.hu_berlin.informatik.utils.optionparser;

import org.apache.commons.cli.Option;

public interface IOptions {
	
	public static final int NO_GROUP = -1;

	public OptionWrapper getOptionWrapper();
	
	/**
	 * @return
	 * the associated Option object
	 */
	default public Option option() {
		return getOptionWrapper().getOption();
	}
	
	/**
	 * @return
	 * the associated group id; if this value is negative, then the option is not part of any group
	 */
	default public int groupId() {
		return getOptionWrapper().getGroupId();
	}
	
	/**
	 * @return
	 * this option, to be used a command line argument (with a '-' in front of the option)
	 */
	default public String asArg() {
		return "-" + option().getOpt();
	}
}
