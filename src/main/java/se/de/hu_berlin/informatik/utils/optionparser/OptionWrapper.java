package se.de.hu_berlin.informatik.utils.optionparser;

import org.apache.commons.cli.Option;

public class OptionWrapper {

	final private Option option;
	final private int groupId;
	
	public OptionWrapper(Option option, int groupId) {
		super();
		this.option = option;
		this.groupId = groupId;
	}

	public Option getOption() {
		return option;
	}

	public int getGroupId() {
		return groupId;
	}
	
}
