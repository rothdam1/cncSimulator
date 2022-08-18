package ch.dcreations.cncsimulator.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Configuration class for Java Logging
 * Reads configuration from property file, specified in the following order:
 * - System property "java.util.logging.config.file" (also used by LogManager)
 * - file "log.properties" in working directory
 * - file "log.properties" in project resources
 * If this class is specified in system property "java.util.logging.config.class" it
 * is loaded automatically at system startup.
 * Otherwise, it can also be called at startup.
 */

public class LogConfiguration {
    private static final Logger logger = Logger.getLogger(LogConfiguration.class.getCanonicalName());

    /*
     * Static class configuration.
     * Only executed once when class is loaded.
     * Load Java logger configuration from config file
     */
    static {
        Locale.setDefault(Locale.ROOT);  // show log messages in english
        // get log config file (default uses log.properties in working directory)
        String logConfigFile = System.getProperty("java.util.logging.config.file", "log.properties");
        Path logConfigPath = Path.of(logConfigFile);
        try {
            InputStream configFileStream;
            if (Files.isReadable(logConfigPath)) {
                // if available and readable use specified file
                configFileStream = Files.newInputStream(logConfigPath);
            } else {
                // otherwise use minimal config from resources
                logConfigFile = "resources:/log.properties";
                configFileStream = ClassLoader.getSystemClassLoader().getResourceAsStream("log.properties");
            }
            if (configFileStream != null) {
                LogManager.getLogManager().readConfiguration(configFileStream);
                logger.fine("Log configuration read from " + logConfigFile);
            } else {
                logger.warning("No log configuration found. Using system default settings.");
            }
        } catch (IOException e) {
            logger.log(Level.FINE, "Error loading log configuration", e);
        }
    }


    public static String getProperty(String name) {
        return LogManager.getLogManager().getProperty(name);
    }

    public static void setLogLevel(Class clazz, Level level) {
        Logger.getLogger(clazz.getCanonicalName()).setLevel(level);
    }

    public static Level getLogLevel(Class clazz) {
        return Logger.getLogger(clazz.getCanonicalName()).getLevel();
    }
}