/**
 * 
 */
package se.de.hu_berlin.informatik.utils.optionparser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import se.de.hu_berlin.informatik.utils.files.FileUtils;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;
import se.de.hu_berlin.informatik.utils.miscellaneous.OutputStreamManipulationUtilities;

/**
 * Provides an easy interface to the commons cli {@link CommandLine} parser
 * which contains less functionality than using the "normal" framework but
 * contains enough functionality for general purposes.
 * 
 * @author Simon Heiden
 * 
 * @see CommandLine
 * @see CommandLineParser
 * @see Options
 * @see Option
 */
final public class OptionParser {

	public enum DefaultCmdOptions implements OptionWrapperInterface {
		/* add options here according to your needs */
		HELP("h", "help", false, "Shows this help.", false),
		SILENCE("z", "silence", false, "Disallows outputs to standard out.", false),
		SILENCE_ALL("zz", "silenceAll", false, "Disallows outputs to standard AND error out.", false),

		THREAD_COUNT("tc", "threadCount", true,
				"Sets a desired thread count manually. Overwrites the default" + " strategy for thread creation.",
				false),
		THREAD_STRATEGY("ts", "threadStrategy", true,
				"Sets a strategy to use regarding the number of threads to create. "
						+ "'AGGRESSIVE' will try to use up to 90% of available processors. "
						+ "'NICE' (default) will try to use up to 50% of available processors. "
						+ "'DEFENSIVE' will only try to use up to 20% of available processors.",
				false);

		/* the following code blocks should not need to be changed */
		final private OptionWrapper option;

		// adds an option that is not part of any group
		DefaultCmdOptions(final String opt, final String longOpt, final boolean hasArg, final String description,
				final boolean required) {
			this.option = new OptionWrapper(
					Option.builder(opt).longOpt(longOpt).required(required).hasArg(hasArg).desc(description).build(),
					NO_GROUP);
		}

		// adds an option that is part of the group with the specified index
		// (positive integer)
		// a negative index means that this option is part of no group
		// this option will not be required, however, the group itself will be
		DefaultCmdOptions(final String opt, final String longOpt, final boolean hasArg, final String description,
				final int groupId) {
			this.option = new OptionWrapper(
					Option.builder(opt).longOpt(longOpt).required(false).hasArg(hasArg).desc(description).build(),
					groupId);
		}

		// adds the given option that will be part of the group with the given
		// id
		DefaultCmdOptions(final Option option, final int groupId) {
			this.option = new OptionWrapper(option, groupId);
		}

		// adds the given option that will be part of no group
		DefaultCmdOptions(final Option option) {
			this(option, NO_GROUP);
		}

		@Override
		public String toString() {
			return option.getOption().getOpt();
		}

		@Override
		public OptionWrapper getOptionWrapper() {
			return option;
		}
	}

	private final static String STRAT_AGGRESSIVE = "AGGRESSIVE";
	private final static String STRAT_NICE = "NICE";
	private final static String STRAT_DEFENSIVE = "DEFENSIVE";

	public enum ThreadingStrategy {
		AGGRESSIVE, NICE, DEFENSIVE;

		@Override
		public String toString() {
			switch (this) {
			case AGGRESSIVE:
				return STRAT_AGGRESSIVE;
			case NICE:
				return STRAT_NICE;
			case DEFENSIVE:
				return STRAT_DEFENSIVE;
			default:
				throw new UnsupportedOperationException("Not implemented.");
			}
		}
	}

	final private HelpFormatter lvFormatter;
	final private CommandLineParser lvParser;
	final private Options lvOptions;
	final private String tool;
	final private String[] args;

	private CommandLine lvCmd;

	private boolean isThreaded = false;

	/**
	 * Parses the options from the command line. This method should be used by
	 * applications to get an OptionParser object.
	 * @param toolName
	 * the name of the tool
	 * @param isThreaded
	 * whether the tool uses threads that run in parallel (provides related
	 * options)
	 * @param options
	 * an Enum containing all the options
	 * @param args
	 * the application's arguments
	 * @return an {@link OptionParser} object that provides access to all parsed
	 * options and their values
	 * @param <T>
	 * an Enum type that represents an option
	 */
	public static <T extends Enum<T> & OptionWrapperInterface> OptionParser getOptions(final String toolName,
			final boolean isThreaded, final Class<T> options, final String... args) {
		final OptionParser optionParser = new OptionParser(toolName, isThreaded, args);
		optionParser.add(options);
		optionParser.parseCommandLine();

		return optionParser;
	}

	/**
	 * Creates an object to easily add and parse options. Creates several
	 * standard options that can be used in any application which uses this
	 * option parser:
	 * 
	 * <br>
	 * <br>
	 * '{@code -h}' prints all possible arguments and usage information, <br>
	 * '{@code -z}' silences the standard output channel, <br>
	 * '{@code -zz}' silences both standard and error output channel.
	 * @param tool
	 * the name of the tool
	 * @param isThreaded
	 * whether the calling application intends to use threads
	 * @param args
	 * the command line arguments that were given to a main function
	 */
	private OptionParser(final String tool, final boolean isThreaded, final String... args) {
		super();
		this.tool = tool;
		this.args = args;
		lvFormatter = new HelpFormatter();
		lvParser = new DefaultParser();
		lvOptions = new Options();

		this.add(DefaultCmdOptions.HELP.option());
		this.addGroup(false, DefaultCmdOptions.SILENCE, DefaultCmdOptions.SILENCE_ALL);

		this.isThreaded = isThreaded;
		if (isThreaded) {
			this.addGroup(false, DefaultCmdOptions.THREAD_COUNT, DefaultCmdOptions.THREAD_STRATEGY);
		}
	}

	/**
	 * Parses the given options.
	 */
	private void parseCommandLine() {
		try {
			lvCmd = this.lvParser.parse(this.lvOptions, this.args);

			if (this.hasOption(DefaultCmdOptions.HELP)) {
				printHelp();
			}

			if (this.hasOption(DefaultCmdOptions.SILENCE)) {
				OutputStreamManipulationUtilities.switchOffStdOutFINAL();
			}

			if (this.hasOption(DefaultCmdOptions.SILENCE_ALL)) {
				OutputStreamManipulationUtilities.switchOffStdOutFINAL();
				OutputStreamManipulationUtilities.switchOffStdErrFINAL();
			}
		} catch (ParseException pvException) {
			Log.err(this, "parse error: %s", pvException.getMessage());
			printHelp();
		}
	}

	/**
	 * Returns a parsed {@link CommandLine} object. Aborts otherwise.
	 * @return parsed {@link CommandLine}
	 */
	private CommandLine getCmdLine() {
		if (lvCmd == null) {
			Log.abort(this, "No command line available. (Maybe forgot parsing the options?)");
		}
		return lvCmd;
	}

	/**
	 * @return the number of threads according to the given options
	 */
	public int getNumberOfThreads() {
		return getNumberOfThreads(0);
	}

	/**
	 * @param minusThreads
	 * a number of threads that are already running (will be subtracted from the
	 * actual returned number)
	 * @return the number of threads according to the given options
	 */
	public int getNumberOfThreads(final int minusThreads) {
		if (!isThreaded) {
			return 1;
		}
		if (this.hasOption(DefaultCmdOptions.THREAD_COUNT)) {
			final int threads = Integer.parseInt(this.getOptionValue(DefaultCmdOptions.THREAD_COUNT)) - minusThreads;
			if (threads < 1) {
				return 1;
			} else {
				return threads;
			}
		}

		ThreadingStrategy strategy = ThreadingStrategy.NICE;
		if (this.hasOption(DefaultCmdOptions.THREAD_STRATEGY)) {
			switch (this.getOptionValue(DefaultCmdOptions.THREAD_STRATEGY)) {
			case STRAT_NICE:
				strategy = ThreadingStrategy.NICE;
				break;
			case STRAT_AGGRESSIVE:
				strategy = ThreadingStrategy.AGGRESSIVE;
				break;
			case STRAT_DEFENSIVE:
				strategy = ThreadingStrategy.DEFENSIVE;
				break;
			default:
				// should not happen
				Log.abort(this, "Unknown strategy: '%s'", this.getOptionValue(DefaultCmdOptions.THREAD_STRATEGY));
			}
		}

		final int processors = Runtime.getRuntime().availableProcessors();
		double processorsToUse;
		switch (strategy) {
		case AGGRESSIVE:
			processorsToUse = (double) processors * 0.9;
			break;
		case DEFENSIVE:
			processorsToUse = (double) processors * 0.2;
			break;
		case NICE:
		default:
			processorsToUse = (double) processors * 0.5;
			break;
		}
		final int threads = (int) Math.round(processorsToUse) - minusThreads;
		if (threads < 1) {
			return 1;
		} else {
			return threads;
		}
	}

	/**
	 * Adds all options declared in the given Enum.
	 * @param options
	 * the options to be added
	 * @param <T>
	 * an Enum type that represents an option
	 */
	private <T extends Enum<T> & OptionWrapperInterface> void add(final Class<T> options) {
		final Map<Integer, List<T>> groups = new HashMap<>();
		for (final T option : EnumSet.allOf(options)) {
			if (option.groupId() < 0) {
				add(option.option());
			} else {
				groups.computeIfAbsent(option.groupId(), k -> new ArrayList<>()).add(option);
			}
		}
		for (final Entry<Integer, List<T>> group : groups.entrySet()) {
			addGroup(true, group.getValue());
		}
	}

	/**
	 * Adds an {@link Option} object.
	 * @param option
	 * the option to be added
	 */
	private void add(final Option option) {
		this.lvOptions.addOption(option);
	}

	/**
	 * Prints the help message and exits with an error message concerning the
	 * given option parameter.
	 * @param opt
	 * the option parameter which produced the error.
	 * @param errorMessage
	 * an error message to display; can be {@code null}
	 * @param <T>
	 * an Enum type that represents an option
	 */
	public <T extends Enum<T> & OptionWrapperInterface> void printHelp(final T opt, String errorMessage) {
		if (errorMessage == null) {
			Log.err(this, "Error with option '%s'.", opt.asArg());
		} else {
			Log.err(this, "Error with option '%s': %s", opt.asArg(), errorMessage);
		}
//		printHelp();
	}

	/**
	 * Prints the help message and exits the application.
	 */
	private void printHelp() {
		this.lvFormatter.printHelp(this.tool, this.lvOptions, true);
		System.exit(1);
	}

	/**
	 * Asserts that at least one of the given options is set. Aborts the program
	 * with an error message otherwise.
	 * @param options
	 * the options
	 * @param <T>
	 * an Enum type that represents an option
	 */
	@SafeVarargs
	public final <T extends Enum<T> & OptionWrapperInterface> void assertAtLeastOneOptionSet(
			final T... options) {
		int count = getNumberOfSetOptions(options);
		if (count < 1) {
			Log.abort(this, "At least one of the options %s has to be set.", getOptionString(options));
		}
	}

	/**
	 * Asserts that exactly one of the given options is set. Aborts the program
	 * with an error message otherwise.
	 * @param options
	 * the options
	 * @param <T>
	 * an Enum type that represents an option
	 */
	@SafeVarargs
	public final <T extends Enum<T> & OptionWrapperInterface> void assertOneOptionSet(
			final T... options) {
		int count = getNumberOfSetOptions(options);
		if (count != 1) {
			Log.abort(this, "Exactly one of the options %s has to be set.", getOptionString(options));
		}
	}

	/**
	 * Asserts that none of the given options are set. Aborts the program with
	 * an error message otherwise.
	 * @param options
	 * the options
	 * @param <T>
	 * an Enum type that represents an option
	 */
	@SafeVarargs
	public final <T extends Enum<T> & OptionWrapperInterface> void assertNoOptionSet(
			final T... options) {
		int count = getNumberOfSetOptions(options);
		if (count != 0) {
			Log.abort(this, "No option of the options %s has to be set.", getOptionString(options));
		}
	}

	/**
	 * Asserts that each of the given options is set. Aborts the program with an
	 * error message otherwise.
	 * @param options
	 * the options
	 * @param <T>
	 * an Enum type that represents an option
	 */
	@SafeVarargs
	public final <T extends Enum<T> & OptionWrapperInterface> void assertAllOptionsSet(
			final T... options) {
		int count = getNumberOfSetOptions(options);
		if (count != options.length) {
			Log.abort(this, "All of the options %s have to be set.", getOptionString(options));
		}
	}

	private <T extends Enum<T> & OptionWrapperInterface> String getOptionString(final T[] options) {
		StringBuilder builder = new StringBuilder("[");
		boolean isFirst = true;
		for (T option : options) {
			if (isFirst) {
				isFirst = false;
			} else {
				builder.append(", ");
			}
			builder.append(option.asArg());
		}
		builder.append("]");
		return builder.toString();
	}

	private <T extends Enum<T> & OptionWrapperInterface> int getNumberOfSetOptions(final T[] options) {
		int count = 0;
		for (T option : options) {
			if (hasOption(option)) {
				++count;
			}
		}
		return count;
	}

	/**
	 * Adds the given option objects to a group and adds the group to the
	 * available options.
	 * @param required
	 * is the option group required?
	 * @param group
	 * the options to be added to the group
	 * @param <T>
	 * an Enum type that represents an option
	 */
	private <T extends Enum<T> & OptionWrapperInterface> void addGroup(final boolean required, final List<T> group) {
		if (!group.isEmpty()) {
			final OptionGroup lvGroup = new OptionGroup();
			for (final T option : group) {
				lvGroup.addOption(option.option());
			}
			lvGroup.setRequired(required);
			lvOptions.addOptionGroup(lvGroup);
		}
	}

	/**
	 * Adds the given option objects to a group and adds the group to the
	 * available options.
	 * @param required
	 * is the option group required?
	 * @param group
	 * the options to be added to the group
	 * @param <T>
	 * an Enum type that represents an option
	 */
	@SafeVarargs
	private final <T extends Enum<T> & OptionWrapperInterface> void addGroup(final boolean required,
																			 final T... group) {
		addGroup(required, new ArrayList<>(Arrays.asList(group)));
	}

	/**
	 * Query to see if an option has been set.
	 * @param opt
	 * an option set with an Enum
	 * @return true if set, false if not
	 * @param <T>
	 * an Enum type that represents an option
	 */
	public <T extends Enum<T> & OptionWrapperInterface> boolean hasOption(final T opt) {
		return getCmdLine().hasOption(opt.option().getOpt());
	}

	/**
	 * Retrieve the first argument, if any, of this option.
	 * @param opt
	 * an option set with an Enum
	 * @return Value of the argument if option is set and has an argument,
	 * otherwise null.
	 * @param <T>
	 * an Enum type that represents an option
	 */
	public <T extends Enum<T> & OptionWrapperInterface> String getOptionValue(final T opt) {
		return getCmdLine().getOptionValue(opt.option().getOpt());
	}

	/**
	 * Retrieve the first argument, if any, of the given option.
	 * @param opt
	 * an option set with an Enum
	 * @param defaultValue
	 * is the default value to be returned if the option is not specified
	 * @return Value of the argument if option is set and has an argument,
	 * otherwise {@code defaultValue}.
	 * @param <T>
	 * an Enum type that represents an option
	 */
	public <T extends Enum<T> & OptionWrapperInterface> String getOptionValue(final T opt, final String defaultValue) {
		return getCmdLine().getOptionValue(opt.option().getOpt(), defaultValue);
	}
	
	/**
	 * Retrieve the first argument, if any, of this option as an integer.
	 * @param opt
	 * an option set with an Enum
	 * @return Value of the argument if option is set and has an argument
	 * and the argument is parseable, otherwise null.
	 * @param <T>
	 * an Enum type that represents an option
	 */
	public <T extends Enum<T> & OptionWrapperInterface> Integer getOptionValueAsInt(final T opt) {
		String value = getCmdLine().getOptionValue(opt.option().getOpt());
		if (value == null) {
			return null;
		}
		try {
			return Integer.valueOf(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Retrieve the first argument, if any, of the given option as an integer.
	 * @param opt
	 * an option set with an Enum
	 * @param defaultValue
	 * is the default value to be returned if the option is not specified
	 * @return Value of the argument if option is set and has an argument
	 * and the argument is parseable, otherwise {@code defaultValue}.
	 * @param <T>
	 * an Enum type that represents an option
	 */
	public <T extends Enum<T> & OptionWrapperInterface> int getOptionValueAsInt(final T opt, final int defaultValue) {
		Integer intValue = getOptionValueAsInt(opt);
		if (intValue == null) {
			return defaultValue;
		} else {
			return intValue;
		}
	}
	
	/**
	 * Retrieve the first argument, if any, of this option as an integer.
	 * @param opt
	 * an option set with an Enum
	 * @return Value of the argument if option is set and has an argument
	 * and the argument is parseable, otherwise null.
	 * @param <T>
	 * an Enum type that represents an option
	 */
	public <T extends Enum<T> & OptionWrapperInterface> Long getOptionValueAsLong(final T opt) {
		String value = getCmdLine().getOptionValue(opt.option().getOpt());
		if (value == null) {
			return null;
		}
		try {
			return Long.valueOf(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Retrieve the first argument, if any, of the given option as an integer.
	 * @param opt
	 * an option set with an Enum
	 * @param defaultValue
	 * is the default value to be returned if the option is not specified
	 * @return Value of the argument if option is set and has an argument
	 * and the argument is parseable, otherwise {@code defaultValue}.
	 * @param <T>
	 * an Enum type that represents an option
	 */
	public <T extends Enum<T> & OptionWrapperInterface> Long getOptionValueAsLong(final T opt, final long defaultValue) {
		Long longValue = getOptionValueAsLong(opt);
		if (longValue == null) {
			return defaultValue;
		} else {
			return longValue;
		}
	}

	// /**
	// * Retrieve the first argument, if any, of this option.
	// * @param opt
	// * an option set with an Enum
	// * @param valueSet
	// * the set of possible arguments; will be compared to the
	// * {@link #toString()} representations of the enum elements
	// * @return Value of the argument if option is set and has an argument,
	// * otherwise null.
	// * @param <T>
	// * an Enum type that represents an option
	// * @param <A>
	// * an Enum type that represents the possible set of arguments
	// */
	// public <T extends Enum<T> & OptionWrapperInterface, A extends Enum<A> &
	// OptionValue<A>> A getOptionValue(
	// final T opt, Class<A> valueSet) {
	// return Misc.getEnumFromToString(valueSet,
	// getCmdLine().getOptionValue(opt.option().getOpt()));
	// }

	/**
	 * Retrieve the first argument, if any, of this option.
	 * @param opt
	 * an option set with an Enum
	 * @param valueSet
	 * the set of possible arguments; will be compared to the
	 * {@link #toString()} representations of the enum elements
	 * @param defaultValue
	 * is the default value to be returned if the option is not specified or can
	 * not be matched to a possible value in the value set
	 * @param abortIfNoMatch
	 * whether to abort the application if the argument does not match any value
	 * in the given set
	 * @return Value of the argument if option is set and has an argument,
	 * otherwise {@code defaultValue}.
	 * @param <T>
	 * an Enum type that represents an option
	 * @param <A>
	 * an Enum type that represents the possible set of arguments
	 */
	public <T extends Enum<T> & OptionWrapperInterface, A extends Enum<A>> A getOptionValue(
			final T opt, Class<A> valueSet, A defaultValue, boolean abortIfNoMatch) {
		String optionValue = getCmdLine().getOptionValue(opt.option().getOpt());
		if (optionValue == null) {
			return defaultValue;
		} else {
			A value = Misc.getEnumFromToString(valueSet, optionValue);
			if (value == null) {
				if (abortIfNoMatch) {
					Log.abort(this, "Unknown option value: '%s'", optionValue);
				} else {
					Log.err(this, "Unknown option value: '%s'", optionValue);
				}
			}
			return value;
		}
	}

	/**
	 * Retrieves the array of values, if any, of the given option.
	 * @param opt
	 * an option set with an Enum
	 * @return Values of the argument if option is set and has an argument,
	 * otherwise null.
	 * @param <T>
	 * an Enum type that represents an option
	 */
	public <T extends Enum<T> & OptionWrapperInterface> String[] getOptionValues(final T opt) {
		return getCmdLine().getOptionValues(opt.option().getOpt());
	}

	/**
	 * Retrieves the array of values, if any, of the given option.
	 * @param opt
	 * an option set with an Enum
	 * @param valueSet
	 * the set of possible arguments; will be compared to the
	 * {@link #toString()} representations of the enum elements
	 * @param abortIfNoMatch
	 * whether to abort the application if the argument does not match any value
	 * in the given set
	 * @return Values of the argument if option is set and has an argument,
	 * otherwise null.
	 * @param <T>
	 * an Enum type that represents an option
	 * @param <A>
	 * an Enum type that represents the possible set of arguments
	 */
	public <T extends Enum<T> & OptionWrapperInterface, A extends Enum<A>> A[] getOptionValues(
			final T opt, Class<A> valueSet, boolean abortIfNoMatch) {
		String[] optionValues = getCmdLine().getOptionValues(opt.option().getOpt());
		if (optionValues == null) {
			return null;
		}
		A[] array = Misc.createGenericArray(valueSet, optionValues.length);
		for (int i = 0; i < optionValues.length; ++i) {
			array[i] = Misc.getEnumFromToString(valueSet, optionValues[i]);
			if (array[i] == null) {
				if (abortIfNoMatch) {
					Log.abort(this, "Unknown option value: '%s'", optionValues[i]);
				} else {
					Log.err(this, "Unknown option value: '%s'", optionValues[i]);
				}
			}
		}
		return array;
	}

	/**
	 * Checks whether the given option is a directory and returns the
	 * corresponding path. Aborts the application if the path is a file (or
	 * doesn't exist if the specific option is set).
	 * @param prefix
	 * a prefix path
	 * @param opt
	 * an option set with an Enum whose value should be a relative path
	 * @param ensureExistence
	 * whether to ensure existence of the directory
	 * @return the path corresponding to the option
	 * @param <T>
	 * an Enum type that represents an option
	 */
	public <T extends Enum<T> & OptionWrapperInterface> Path isDirectory(final Path prefix, final T opt,
			final boolean ensureExistence) {
		Path path;
		if (ensureExistence) {
			path = FileUtils.checkIfAnExistingDirectory(prefix, getOptionValue(opt));
		} else {
			path = FileUtils.checkIfNotAnExistingFile(prefix, getOptionValue(opt));
		}

		if (path == null) {
			if (ensureExistence) {
				printHelp(opt, String.format("Directory '%s' does not exist or is an existing file.", getOptionValue(opt)));
			} else {
				printHelp(opt, String.format("Directory '%s' is an existing file.", getOptionValue(opt)));
			}
		}

		return path;
	}

	/**
	 * Checks whether the given option is a file and returns the corresponding
	 * path. Aborts the application if the path is a directory (or doesn't exist
	 * if the specific option is set).
	 * @param prefix
	 * a prefix path
	 * @param opt
	 * an option set with an Enum whose value should be a relative path
	 * @param ensureExistence
	 * whether to ensure existence of the file
	 * @return the path corresponding to the option
	 * @param <T>
	 * an Enum type that represents an option
	 */
	public <T extends Enum<T> & OptionWrapperInterface> Path isFile(final Path prefix, final T opt,
			final boolean ensureExistence) {
		Path path;
		if (ensureExistence) {
			path = FileUtils.checkIfAnExistingFile(prefix, getOptionValue(opt));
		} else {
			path = FileUtils.checkIfNotAnExistingDirectory(prefix, getOptionValue(opt));
		}

		if (path == null) {
			if (ensureExistence) {
				printHelp(opt, String.format("File '%s' does not exist or is an existing directory.", getOptionValue(opt)));
			} else {
				printHelp(opt, String.format("File '%s' is an existing directory.", getOptionValue(opt)));
			}
		}

		return path;
	}

	/**
	 * Checks whether the given option is a directory and returns the
	 * corresponding path. Aborts the application if the path is a file (or
	 * doesn't exist if the specific option is set).
	 * @param opt
	 * an option set with an Enum whose value should be a relative path
	 * @param ensureExistence
	 * whether to ensure existence of the directory
	 * @return the path corresponding to the option
	 * @param <T>
	 * an Enum type that represents an option
	 */
	public <T extends Enum<T> & OptionWrapperInterface> Path isDirectory(final T opt, final boolean ensureExistence) {
		return isDirectory(null, opt, ensureExistence);
	}

	/**
	 * Checks whether the given option is a file and returns the corresponding
	 * path. Aborts the application if the path is a directory (or doesn't exist
	 * if the specific option is set).
	 * @param opt
	 * an option set with an Enum whose value should be a relative path
	 * @param ensureExistence
	 * whether to ensure existence of the file
	 * @return the path corresponding to the option
	 * @param <T>
	 * an Enum type that represents an option
	 */
	public <T extends Enum<T> & OptionWrapperInterface> Path isFile(final T opt, final boolean ensureExistence) {
		return isFile(null, opt, ensureExistence);
	}

}
