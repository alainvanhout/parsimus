package parsimus;

import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class manages all request thread full log stacks, via static methods for initialization of the request thread,
 * adding of log entries to the full log stack, specifying that the full log stack should be printed (via @Link active),
 * and printing the current log stack (if @Link active) has been set to true for the current request thread).
 */
public class ParsimusLoggingManager {

    /**
     * Actual slf4j Logger, which is used to print the full log stack
     */
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(Parsimus.class);

    /**
     * The full log stack for each request thread
     */
    private static ThreadLocal<List<LogEntry>> logEntries = new ThreadLocal<>();

    /**
     * Initializes or resets the full log stack and @Link active state for the current request thread
     */
    public static void reset() {
        logEntries.set(new ArrayList<>());
    }

    /**
     * Add a new entry to the full log stack, based on the supplied parameters.
     *
     * @param name   The name associated with the slf4j logger and proxy that made the log call
     * @param method The method that was called on the logger
     * @param args   The arguments that were passed to the log call
     */
    public static void add(String name, Method method, Object[] args) {
        // get the log stack for the current request thread
        List<LogEntry> entries = getLogEntries();
        // add the call to the full log stack
        entries.add(new LogEntry(name, method, args, LocalDateTime.now()));
    }

    public static void print() {

        List<LogEntry> entries = getLogEntries();

        LOG.warn("==== STARTING PARSIMUS LOGGING AT LEVEL: " + getLogLevel() + " ====");

        for (LogEntry logEntry : entries) {
            Object[] args = logEntry.getArgs();
            // add the information about the original Logger's associated class
            // and the time at which the log call was made
            extendFirstArgument(logEntry, args);

            invoke(logEntry.getMethod(), args);
        }

        LOG.info("===== FINISHED PARSIMUS LOGGING =====");

        // clear the log stack for the current request thread
        reset();
    }

    private static void extendFirstArgument(LogEntry logEntry, Object[] args) {
        if (args.length > 0 && args[0] instanceof String) {
            args[0] = String.format("[%s %s] %s",
                    logEntry.getLoggerName(),
                    logEntry.getDateTime().format(DateTimeFormatter.ISO_DATE_TIME),
                    args[0]);
        }
    }

    private static void invoke(Method method, Object[] args) {
        try {
            method.invoke(LOG, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getLogLevel() {
        if (LOG.isTraceEnabled()) {
            return "TRACE";
        }
        if (LOG.isDebugEnabled()) {
            return "DEBUG";
        }
        if (LOG.isInfoEnabled()) {
            return "INFO";
        }
        if (LOG.isWarnEnabled()) {
            return "WARN";
        }
        if (LOG.isErrorEnabled()) {
            return "ERROR";
        }
        return "NONE";
    }

    private static List<LogEntry> getLogEntries() {
        if (logEntries.get() == null) {
            logEntries.set(new ArrayList<>());
        }

        return logEntries.get();
    }
}