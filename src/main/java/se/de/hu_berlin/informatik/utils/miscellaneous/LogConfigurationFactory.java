package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.net.URI;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(name = "LogConfigurationFactory", category = ConfigurationFactory.CATEGORY)
@Order(50)
public class LogConfigurationFactory extends ConfigurationFactory {

    static Configuration createConfiguration(LoggerContext arg0, final String name, ConfigurationBuilder<BuiltConfiguration> builder) {
    	if (arg0 != null) {
    		builder.setLoggerContext(arg0);
    	}
		builder.setConfigurationName("Log-config");
        builder.setStatusLevel(Level.ERROR);
        builder.add(builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.NEUTRAL).
            addAttribute("level", Level.DEBUG));
        AppenderComponentBuilder appenderBuilder = builder.newAppender("Stdout", "CONSOLE").
            addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
        appenderBuilder.add(builder.newLayout("PatternLayout").
        		addAttribute("noConsoleNoAnsi", true).
            addAttribute("pattern", "%highlight{%d{HH:mm:ss} %-5level [%c{1}] %msg%n"
            		+ "%xEx{filters(org.junit,org.eclipse.jdt,org.apache.maven,sun.reflect,java.lang.reflect,se.de.hu_berlin.informatik.utils.miscellaneous.Log)}}"));
        appenderBuilder.add(builder.newFilter("MarkerFilter", Filter.Result.DENY,
            Filter.Result.NEUTRAL).addAttribute("marker", "FLOW"));
        builder.add(appenderBuilder);
        builder.add(builder.newLogger("org.apache.logging.log4j", Level.DEBUG).
            add(builder.newAppenderRef("Stdout")).
            addAttribute("additivity", false));
        builder.add(builder.newRootLogger(Level.ERROR).add(builder.newAppenderRef("Stdout")));
        
        return builder.build();
    }

    @Override
    protected String[] getSupportedTypes() {
        return new String[] {"*"};
    }

    @Override
    public Configuration getConfiguration(LoggerContext arg0, final ConfigurationSource source) {
        return getConfiguration(arg0, source.toString(), null);
    }

    @Override
    public Configuration getConfiguration(LoggerContext arg0, final String name, final URI configLocation) {
        return getConfiguration(arg0, name, configLocation, null);
    }

	@Override
	public Configuration getConfiguration(LoggerContext arg0, String name, URI configLocation, ClassLoader arg3) {
		ConfigurationBuilder<BuiltConfiguration> builder = newConfigurationBuilder();
        return createConfiguration(arg0, name, builder);
	}

    
    
    
}
