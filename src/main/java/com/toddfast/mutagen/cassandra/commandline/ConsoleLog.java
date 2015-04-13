package com.toddfast.mutagen.cassandra.commandline;

import com.toddfast.mutagen.cassandra.util.logging.Log;

/**
 * Wrapper around a simple Console output.
 */
public class ConsoleLog implements Log {
    public static enum Level {
        DEBUG, INFO, WARN
    }

    private final Level level;

    /**
     * Creates a new Console Log.
     *
     * @param level
     *            the log level.
     */
    public ConsoleLog(Level level) {
        this.level = level;
    }

    public void debug(String message) {
        if (level == Level.DEBUG) {
            System.out.println("DEBUG: " + message);
        }
    }

    public void info(String message) {
        if (level.compareTo(Level.INFO) <= 0) {
            System.out.println(message);
        }
    }

    public void warn(String message) {
        System.out.println("WARNING: " + message);
    }

    public void error(String message) {
        System.out.println("ERROR: " + message);
    }

    public void error(String message, Exception e) {
        System.out.println("ERROR: " + message);
        e.printStackTrace();
    }

}
