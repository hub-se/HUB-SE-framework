/**
 * 
 */
package se.de.hu_berlin.informatik.utils.optionparser;

import java.nio.file.Path;
import java.nio.file.Paths;

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
public class OptionParser {
	
	final private HelpFormatter lvFormatter;
    final private CommandLineParser lvParser;
    final private Options lvOptions;
    final private String tool;
    final private String[] args;
    
    private CommandLine lvCmd;
	
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
     * @param args
     * the command line arguments that were given to a main function
     */
	public OptionParser(final String tool, final String[] args) {
		super();
		this.tool = tool;
		this.args = args;
		lvFormatter = new HelpFormatter();
		lvParser = new DefaultParser();
		lvOptions = new Options();
		
		this.add("h", "help", false, "Shows this help.");
		this.add("z", "silence", false, "Disallows outputs to standard out.");
		this.add("zz", "silenceAll", false, "Disallows outputs to standard AND error out.");
	}
	
	/**
	 * Parses the given options.
	 */
	public void parseCommandLine() {
		try {
			lvCmd = this.lvParser.parse(this.lvOptions, this.args);

            if (lvCmd.hasOption('h')) {
            	printHelp(0);
            }
            
            if (lvCmd.hasOption('z')) {
            	OutputStreamManipulationUtilities.switchOffStdOutFINAL();
            }
            
            if (lvCmd.hasOption("zz")) {
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
	 * Adds an {@link Option} object.
	 * @param option
	 * the option to be added
	 */
	public void add(final Option option) {
		this.lvOptions.addOption(option);
	}
	
	/**
	 * Adds an optional (not required) option with the specified attributes.
	 * @param opt
	 * short descriptor
	 * @param longOpt
	 * long descriptor
	 * @param hasArg
	 * does the option expect an argument?
	 * @param description
	 * description of the option
	 */
	public void add(final String opt, final String longOpt, final boolean hasArg, final String description) {
		this.lvOptions.addOption(new Option(opt, longOpt, hasArg, description));
	}
	
	/**
	 * Adds an option with the specified attributes and also sets the 'required' attribute.
	 * @param opt
	 * short descriptor
	 * @param longOpt
	 * long descriptor
	 * @param hasArg
	 * does the option expect an argument?
	 * @param description
	 * description of the option
	 * @param required
	 * is the option required?
	 */
	public void add(final String opt, final String longOpt, final boolean hasArg, final String description, final boolean required) {
		Option option = new Option(opt, longOpt, hasArg, description);
		option.setRequired(required);
		this.lvOptions.addOption(option);
	}
	
	/**
	 * Prints the help message and exits with the given status code
	 * and an error message concerning the given option parameter.
	 * @param status
	 * exit status code
	 * @param opt
	 * the option parameter which produced the error.
	 */
	public void printHelp(final int status, String opt) {
		Log.err(this, "Error with option '-%s'.", opt);
		printHelp(status);
	}
	
	/**
	 * Prints the help message and exits with the given status code
	 * and an error message concerning the given option parameter.
	 * @param status
	 * exit status code
	 * @param opt
	 * the option parameter which produced the error.
	 */
	public void printHelp(final int status, char opt) {
		Log.err(this, "Error with option '-%s'.", opt);
		printHelp(status);
	}
	
	/**
	 * Prints the help message and exits with the given status code.
	 * @param status
	 * exit status code
	 */
	public void printHelp(final int status) {
		this.lvFormatter.printHelp(this.tool, this.lvOptions, true);
        System.exit(status);
	}

	/**
	 * Adds the given {@link OptionGroup}.
	 * @param lvGroup
	 * the option group to be added
	 */
	public void addGroup(final OptionGroup lvGroup) {
        lvOptions.addOptionGroup(lvGroup);
	}
	
	/**
	 * Adds the given {@link Option} objects to a group and adds the group to the available options.
	 * @param required
	 * is the option group required?
	 * @param options
	 * the options to be added to the group
	 */
	public void addGroup(boolean required, Option... options) {
		if (options.length > 0) {
			OptionGroup lvGroup = new OptionGroup();
			for (int i = 0; i < options.length; ++i) {
				lvGroup.addOption(options[i]);
			}
			lvGroup.setRequired(required);
			lvOptions.addOptionGroup(lvGroup);
		}
	}
	
	/**
	 * Adds the given {@link Option} objects to a group and adds the group to the available options.
	 * The group is not required.
	 * @param options
	 * the options to be added to the group
	 */
	public void addGroup(Option... options) {
		addGroup(false, options);
	}
	
	/**
	 * Adds the given option group that is specified by the two sets of attributes. The group is not required.
	 * @param opt1
	 * short descriptor of option 1
	 * @param longOpt1
	 * long descriptor of option 1
	 * @param hasArg1
	 * does option 1 expect an argument?
	 * @param description1
	 * description of option 1
	 * @param opt2
	 * short descriptor of option 2
	 * @param longOpt2
	 * long descriptor of option 2
	 * @param hasArg2
	 * does option 2 expect an argument?
	 * @param description2
	 * description of option 2
	 */
	public void addGroup(final String opt1, final String longOpt1, final boolean hasArg1, final String description1, 
			final String opt2, final String longOpt2, final boolean hasArg2, final String description2) {
		addGroup(opt1, longOpt1, hasArg1, description1, opt2, longOpt2, hasArg2, description2, false);
	}
	
	/**
	 * Adds the given option group that is specified by the two sets of attributes and the 'required' attribute.
	 * @param opt1
	 * short descriptor of option 1
	 * @param longOpt1
	 * long descriptor of option 1
	 * @param hasArg1
	 * does option 1 expect an argument?
	 * @param description1
	 * description of option 1
	 * @param opt2
	 * short descriptor of option 2
	 * @param longOpt2
	 * long descriptor of option 2
	 * @param hasArg2
	 * does option 2 expect an argument?
	 * @param description2
	 * description of option 2
	 * @param required
	 * is the option group required?
	 */
	public void addGroup(final String opt1, final String longOpt1, final boolean hasArg1, final String description1, 
			final String opt2, final String longOpt2, final boolean hasArg2, final String description2, final boolean required) {
		OptionGroup lvGroup = new OptionGroup();
        Option opt_one = new Option(opt1, longOpt1, hasArg1, description1);
        Option opt_two = new Option(opt2, longOpt2, hasArg2, description2);
        lvGroup.addOption(opt_one);
        lvGroup.addOption(opt_two);
        lvGroup.setRequired(required);
        lvOptions.addOptionGroup(lvGroup);
	}
	
	/**
	 * Query to see if an option has been set.
	 * @param opt
	 * the character name of the option
	 * @return
	 * true if set, false if not
	 */
	public boolean hasOption(char opt) {
		return getCmdLine().hasOption(opt);
	}
	
	/**
	 * Query to see if an option has been set.
	 * @param opt
	 * the name of the option
	 * @return
	 * true if set, false if not
	 */
	public boolean hasOption(String opt) {
		return getCmdLine().hasOption(opt);
	}
	
	/**
	 * Retrieve the first argument, if any, of this option.
	 * @param opt
	 * the character name of the option
	 * @return
	 * Value of the argument if option is set and has an argument, 
	 * otherwise null.
	 */
	public String getOptionValue(char opt) {
		return getCmdLine().getOptionValue(opt);
	}
	
	/**
	 * Retrieve the first argument, if any, of the given option.
	 * @param opt
	 * the name of the option
	 * @return
	 * Value of the argument if option is set and has an argument, 
	 * otherwise null.
	 */
	public String getOptionValue(String opt) {
		return getCmdLine().getOptionValue(opt);
	}
	
	/**
	 * Retrieve the first argument, if any, of the given option.
	 * @param opt
	 * the character name of the option
	 * @param defaultValue
	 * is the default value to be returned if the option is not specified
	 * @return
	 * Value of the argument if option is set and has an argument,
	 * otherwise {@code defaultValue}.
	 */
	public String getOptionValue(char opt, String defaultValue) {
		return getCmdLine().getOptionValue(opt, defaultValue);
	}
	
	/**
	 * Retrieve the first argument, if any, of the given option.
	 * @param opt
	 * the name of the option
	 * @param defaultValue
	 * is the default value to be returned if the option is not specified
	 * @return
	 * Value of the argument if option is set and has an argument,
	 * otherwise {@code defaultValue}.
	 */
	public String getOptionValue(String opt, String defaultValue) {
		return getCmdLine().getOptionValue(opt, defaultValue);
	}
	
	/**
	 * Retrieves the array of values, if any, of the given option.
	 * @param opt
	 * the character name of the option
	 * @return
	 * Values of the argument if option is set and has an argument, 
	 * otherwise null.
	 */
	public String[] getOptionValues(char opt) {
		return getCmdLine().getOptionValues(opt);
	}
	
	/**
	 * Retrieves the array of values, if any, of the given option.
	 * @param opt
	 * the name of the option
	 * @return
	 * Values of the argument if option is set and has an argument, 
	 * otherwise null.
	 */
	public String[] getOptionValues(String opt) {
		return getCmdLine().getOptionValues(opt);
	}
	
	/**
	 * Checks whether the given option is a directory and returns
	 * the corresponding path. Aborts the application if the path
	 * is a file (or doesn't exist if the specific option is set).
	 * @param prefix
	 * a prefix path
	 * @param opt
	 * an option parameter whose value should be a relative path
	 * @param ensureExistence
	 * whether to ensure existence of the directory
	 * @return
	 * the path corresponding to the option
	 */
	public Path isDirectory(Path prefix, String opt, boolean ensureExistence) {
		Path path = null;
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
	 * an option parameter whose value should be a relative path
	 * @param ensureExistence
	 * whether to ensure existence of the file
	 * @return
	 * the path corresponding to the option
	 */
	public Path isFile(Path prefix, String opt, boolean ensureExistence) {
		Path path = null;
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
	 * an option parameter whose value should be a relative path
	 * @param ensureExistence
	 * whether to ensure existence of the directory
	 * @return
	 * the path corresponding to the option
	 */
	public Path isDirectory(String opt, boolean ensureExistence) {
		return isDirectory(null, opt, ensureExistence);
	}
	
	/**
	 * Checks whether the given option is a file and returns
	 * the corresponding path. Aborts the application if the path
	 * is a directory (or doesn't exist if the specific option is set).
	 * @param opt
	 * an option parameter whose value should be a relative path
	 * @param ensureExistence
	 * whether to ensure existence of the file
	 * @return
	 * the path corresponding to the option
	 */
	public Path isFile(String opt, boolean ensureExistence) {
		return isFile(null, opt, ensureExistence);
	}
	
	/**
	 * Checks whether the given option is a directory and returns
	 * the corresponding path. Aborts the application if the path
	 * is a file (or doesn't exist if the specific option is set).
	 * @param opt
	 * an option parameter whose value should be a relative path
	 * @param ensureExistence
	 * whether to ensure existence of the directory
	 * @return
	 * the path corresponding to the option
	 */
	public Path isDirectory(char opt, boolean ensureExistence) {
		return isDirectory(null, String.valueOf(opt), ensureExistence);
	}
	
	/**
	 * Checks whether the given option is a file and returns
	 * the corresponding path. Aborts the application if the path
	 * is a directory (or doesn't exist if the specific option is set).
	 * @param opt
	 * an option parameter whose value should be a relative path
	 * @param ensureExistence
	 * whether to ensure existence of the file
	 * @return
	 * the path corresponding to the option
	 */
	public Path isFile(char opt, boolean ensureExistence) {
		return isFile(null, String.valueOf(opt), ensureExistence);
	}
	
	
}
