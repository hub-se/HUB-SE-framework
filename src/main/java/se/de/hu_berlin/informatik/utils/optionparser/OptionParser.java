/**
 * 
 */
package se.de.hu_berlin.informatik.utils.optionparser;

import java.nio.file.Path;
import java.nio.file.Paths;
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

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
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
	
	public static enum DefaultCmdOptions implements OptionWrapperInterface {
		/* add options here according to your needs */
		HELP("h", "help", false, "Shows this help.", false),
		SILENCE("z", "silence", false, "Disallows outputs to standard out.", false),
		SILENCE_ALL("zz", "silenceAll", false, "Disallows outputs to standard AND error out.", false),

		THREAD_COUNT("tc", "threadCount", true, "Sets a desired thread count manually. Overwrites the default"
				+ " strategy for thread creation.", false),
		THREAD_STRATEGY("ts", "threadStrategy", true, "Sets a strategy to use regarding the number of threads to create. "
						+ "'AGGRESSIVE' will try to use up to 90% of available processors. "
						+ "'NICE' (default) will try to use up to 50% of available processors. "
						+ "'DEFENSIVE' will only try to use up to 20% of available processors.", false);
		
		/* the following code blocks should not need to be changed */
		final private OptionWrapper option;

		//adds an option that is not part of any group
		DefaultCmdOptions(final String opt, final String longOpt, 
				final boolean hasArg, final String description, final boolean required) {
			this.option = new OptionWrapper(
					Option.builder(opt).longOpt(longOpt).required(required).
					hasArg(hasArg).desc(description).build(),
					NO_GROUP);
		}
		
		//adds an option that is part of the group with the specified index (positive integer)
		//a negative index means that this option is part of no group
		//this option will not be required, however, the group itself will be
		DefaultCmdOptions(final String opt, final String longOpt, 
				final boolean hasArg, final String description, final int groupId) {
			this.option = new OptionWrapper(
					Option.builder(opt).longOpt(longOpt).required(false).
					hasArg(hasArg).desc(description).build(),
					groupId);
		}
		
		//adds the given option that will be part of the group with the given id
		DefaultCmdOptions(final Option option, final int groupId) {
			this.option = new OptionWrapper(option, groupId);
		}
		
		//adds the given option that will be part of no group
		DefaultCmdOptions(final Option option) {
			this(option, NO_GROUP);
		}

		@Override public String toString() { return option.getOption().getOpt(); }
		@Override public OptionWrapper getOptionWrapper() { return option; }
	}
	
	public final static String STRAT_AGGRESSIVE = "AGGRESSIVE";
	public final static String STRAT_NICE = "NICE";
	public final static String STRAT_DEFENSIVE = "DEFENSIVE";
	
	public enum ThreadingStrategy { AGGRESSIVE(0), NICE(1), DEFENSIVE(2);
		private final int id;
		private ThreadingStrategy(int id) {
			this.id = id;
		}

		@Override
		public String toString() {
			switch(id) {
			case 0:
				return STRAT_AGGRESSIVE;
			case 1:
				return STRAT_NICE;
			case 2:
				return STRAT_DEFENSIVE;
			default:
				return STRAT_NICE;
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
	 * Parses the options from the command line. This method should be used by applications to get an OptionParser object.
	 * @param toolName
	 * the name of the tool
	 * @param isThreaded
	 * whether the tool uses threads that run in parallel (provides related options)
	 * @param options
	 * an Enum containing all the options
	 * @param args
	 * the application's arguments
	 * @return
	 * an {@link OptionParser} object that provides access to all parsed options and their values
	 * @param <T>
	 * an Enum type that represents an option
	 */
	public static <T extends Enum<T> & OptionWrapperInterface> OptionParser getOptions(
			final String toolName, final boolean isThreaded, final Class<T> options, final String... args) {
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
     * <br><br> '{@code -h}' prints all possible arguments and usage information,
     * <br> '{@code -z}' silences the standard output channel,
     * <br> '{@code -zz}' silences both standard and error output channel.
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
            	printHelp(0);
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
            printHelp(1);
        }
	}
	
	/**
	 * Returns a parsed {@link CommandLine} object. Aborts otherwise.
	 * @return
	 * parsed {@link CommandLine}
	 */
	public CommandLine getCmdLine() {
		if (lvCmd == null) {
			Log.abort(this, "No command line available. (Maybe forgot parsing the options?)");
		}
		return lvCmd;
	}
	
	/**
	 * @return
	 * the number of threads according to the given options
	 */
	public int getNumberOfThreads() {
		return getNumberOfThreads(0);
	}
	
	/**
	 * @param minusThreads
	 * a number of threads that are already running
	 * (will be subtracted from the actual returned number)
	 * @return
	 * the number of threads according to the given options
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
			switch(this.getOptionValue(DefaultCmdOptions.THREAD_STRATEGY)) {
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
				//should not happen
				Log.abort(this, "Unknown strategy: '%s'", this.getOptionValue(DefaultCmdOptions.THREAD_STRATEGY));
			}
		}
		
		final int processors = Runtime.getRuntime().availableProcessors();
		double processorsToUse;		
		switch(strategy) {
		case AGGRESSIVE:
			processorsToUse = (double)processors * 0.9;
			break;
		case DEFENSIVE:
			processorsToUse = (double)processors * 0.2;
			break;
		case NICE:
		default:
			processorsToUse = (double)processors * 0.5;
			break;
		}
		final int threads = (int)Math.round(processorsToUse) - minusThreads;
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
	 * Prints the help message and exits with the given status code
	 * and an error message concerning the given option parameter.
	 * @param status
	 * exit status code
	 * @param opt
	 * the option parameter which produced the error.
	 * @param <T>
	 * an Enum type that represents an option
	 */
	public <T extends Enum<T> & OptionWrapperInterface> void printHelp(
			final int status, final T opt) {
		Log.err(this, "Error with option '%s'.", opt.asArg());
		printHelp(status);
	}
	
	/**
	 * Prints the help message and exits with the given status code.
	 * @param status
	 * exit status code
	 */
	private void printHelp(final int status) {
		this.lvFormatter.printHelp(this.tool, this.lvOptions, true);
        System.exit(status);
	}
	
	public <T extends Enum<T> & OptionWrapperInterface> void assertAtLeastOneOptionSet(
			@SuppressWarnings("unchecked") final T... options) {
		int count = getNumberOfSetOptions(options);
		if (count < 1) {
			Log.abort(this, "At least one of the options %s has to be set.", getOptionString(options));
		}
	}
	
	public <T extends Enum<T> & OptionWrapperInterface> void assertOneOptionSet(
			@SuppressWarnings("unchecked") final T... options) {
		int count = getNumberOfSetOptions(options);
		if (count != 1) {
			Log.abort(this, "Exactly one of the options %s has to be set.", getOptionString(options));
		}
	}
	
	public <T extends Enum<T> & OptionWrapperInterface> void assertNoOptionSet(
			@SuppressWarnings("unchecked") final T... options) {
		int count = getNumberOfSetOptions(options);
		if (count != 0) {
			Log.abort(this, "No option of the options %s has to be set.", getOptionString(options));
		}
	}
	
	public <T extends Enum<T> & OptionWrapperInterface> void assertAllOptionsSet(
			@SuppressWarnings("unchecked") final T... options) {
		int count = getNumberOfSetOptions(options);
		if (count != options.length) {
			Log.abort(this, "All of the options %s have to be set.", getOptionString(options));
		}
	}

	private <T extends Enum<T> & OptionWrapperInterface> String getOptionString(final T[] options) {
		StringBuilder builder = new StringBuilder();
		boolean isFirst = true;
		for (T option : options) {
			if (isFirst) {
				isFirst = false;
			} else {
				builder.append(", ");
			}
			builder.append(option.asArg());
		}
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
	 * Adds the given option objects to a group and adds the group to the available options.
	 * @param required
	 * is the option group required?
	 * @param group
	 * the options to be added to the group
	 * @param <T>
	 * an Enum type that represents an option
	 */
	private <T extends Enum<T> & OptionWrapperInterface> void addGroup(
			final boolean required, final List<T> group) {
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
	 * Adds the given option objects to a group and adds the group to the available options.
	 * @param required
	 * is the option group required?
	 * @param group
	 * the options to be added to the group
	 * @param <T>
	 * an Enum type that represents an option
	 */
	private <T extends Enum<T> & OptionWrapperInterface> void addGroup(
			final boolean required, @SuppressWarnings("unchecked") final T... group) {
		addGroup(required, new ArrayList<T>(Arrays.asList(group)));
	}
	
	/**
	 * Query to see if an option has been set.
	 * @param opt
	 * an option set with an Enum
	 * @return
	 * true if set, false if not
	 * @param <T>
	 * an Enum type that represents an option
	 */
	public <T extends Enum<T> & OptionWrapperInterface> boolean hasOption(
			final T opt) {
		return getCmdLine().hasOption(opt.option().getOpt());
	}
	
	/**
	 * Retrieve the first argument, if any, of this option.
	 * @param opt
	 * an option set with an Enum
	 * @return
	 * Value of the argument if option is set and has an argument, 
	 * otherwise null.
	 * @param <T>
	 * an Enum type that represents an option
	 */
	public <T extends Enum<T> & OptionWrapperInterface> String getOptionValue(
			final T opt) {
		return getCmdLine().getOptionValue(opt.option().getOpt());
	}
	
	/**
	 * Retrieve the first argument, if any, of the given option.
	 * @param opt
	 * an option set with an Enum
	 * @param defaultValue
	 * is the default value to be returned if the option is not specified
	 * @return
	 * Value of the argument if option is set and has an argument,
	 * otherwise {@code defaultValue}.
	 * @param <T>
	 * an Enum type that represents an option
	 */
	public <T extends Enum<T> & OptionWrapperInterface> String getOptionValue(
			final T opt, final String defaultValue) {
		return getCmdLine().getOptionValue(opt.option().getOpt(), defaultValue);
	}
	
	/**
	 * Retrieves the array of values, if any, of the given option.
	 * @param opt
	 * an option set with an Enum
	 * @return
	 * Values of the argument if option is set and has an argument, 
	 * otherwise null.
	 * @param <T>
	 * an Enum type that represents an option
	 */
	public <T extends Enum<T> & OptionWrapperInterface> String[] getOptionValues(
			final T opt) {
		return getCmdLine().getOptionValues(opt.option().getOpt());
	}
	
	/**
	 * Checks whether the given option is a directory and returns
	 * the corresponding path. Aborts the application if the path
	 * is a file (or doesn't exist if the specific option is set).
	 * @param prefix
	 * a prefix path
	 * @param opt
	 * an option set with an Enum whose value should be a relative path
	 * @param ensureExistence
	 * whether to ensure existence of the directory
	 * @return
	 * the path corresponding to the option
	 * @param <T>
	 * an Enum type that represents an option
	 */
	public <T extends Enum<T> & OptionWrapperInterface> Path isDirectory(
			final Path prefix, final T opt, final boolean ensureExistence) {
		Path path;
		if (prefix != null) {
			path = prefix.resolve(Paths.get(getOptionValue(opt)));
		} else {
			path = Paths.get(getOptionValue(opt));
		}
		
		if ((ensureExistence && !path.toFile().exists()) || 
				(path.toFile().exists() && !path.toFile().isDirectory())) {
			printHelp(1, opt);
		}
		
		return path;
	}
	
	/**
	 * Checks whether the given option is a file and returns
	 * the corresponding path. Aborts the application if the path
	 * is a directory (or doesn't exist if the specific option is set).
	 * @param prefix
	 * a prefix path
	 * @param opt
	 * an option set with an Enum whose value should be a relative path
	 * @param ensureExistence
	 * whether to ensure existence of the file
	 * @return
	 * the path corresponding to the option
	 * @param <T>
	 * an Enum type that represents an option
	 */
	public <T extends Enum<T> & OptionWrapperInterface> Path isFile(
			final Path prefix, final T opt, final boolean ensureExistence) {
		Path path;
		if (prefix != null) {
			path = prefix.resolve(Paths.get(getOptionValue(opt)));
		} else {
			path = Paths.get(getOptionValue(opt));
		}
		
		if ((ensureExistence && !path.toFile().exists()) || 
				path.toFile().isDirectory()) {
			printHelp(1, opt);
		}
		
		return path;
	}
	
	/**
	 * Checks whether the given option is a directory and returns
	 * the corresponding path. Aborts the application if the path
	 * is a file (or doesn't exist if the specific option is set).
	 * @param opt
	 * an option set with an Enum whose value should be a relative path
	 * @param ensureExistence
	 * whether to ensure existence of the directory
	 * @return
	 * the path corresponding to the option
	 * @param <T>
	 * an Enum type that represents an option
	 */
	public <T extends Enum<T> & OptionWrapperInterface> Path isDirectory(
			final T opt, final boolean ensureExistence) {
		return isDirectory(null, opt, ensureExistence);
	}
	
	/**
	 * Checks whether the given option is a file and returns
	 * the corresponding path. Aborts the application if the path
	 * is a directory (or doesn't exist if the specific option is set).
	 * @param opt
	 * an option set with an Enum whose value should be a relative path
	 * @param ensureExistence
	 * whether to ensure existence of the file
	 * @return
	 * the path corresponding to the option
	 * @param <T>
	 * an Enum type that represents an option
	 */
	public <T extends Enum<T> & OptionWrapperInterface> Path isFile(
			final T opt, final boolean ensureExistence) {
		return isFile(null, opt, ensureExistence);
	}
	
}
