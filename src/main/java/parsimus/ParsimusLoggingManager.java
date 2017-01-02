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
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(ParsimusLoggingManager.class);

    /**
     * The full log stack for each request thread
     */
    private static ThreadLocal<List<LogEntry>> logEntries = new ThreadLocal<>();

    /**
     * Flag which determines whether the full log stack of the current request thread should be printed once the request
     * thread has passed the filter chaining in @{@link parsimus.servlet.ParsimusLoggingFilter}
     */
    private static ThreadLocal<Boolean> active = new ThreadLocal<>();

    /**
     * Initializes or resets the full log stack and @Link active state for the current request thread
     */
    public static void init() {
        logEntries.set(new ArrayList<>());
        active.set(false);
    }

    /**
     * Add a new entry to the full log stack, based on the supplied parameters.
     * @param clazz The class associated with the slf4j logger and proxy that made the log call
     * @param method The method that was called on the logger
     * @param args The arguments that were passed to the log call
     */
    public static void add(Class clazz, Method method, Object[] args) {
        // get the log stack for the current request thread
        List<LogEntry> entries = ParsimusLoggingManager.logEntries.get();
        // add the call to the full log stack
        entries.add(new LogEntry(clazz, method, args, LocalDateTime.now()));
    }

    public static void printAll() {
        // do
        if (active.get()) {
            List<LogEntry> entries = ParsimusLoggingManager.logEntries.get();

            LOG.info("===== STARTING THREAD LOGGING PRINT =====");

            for (LogEntry logEntry : entries) {

                Method method = logEntry.getMethod();
                Object[] args = logEntry.getArgs();

                // add the information about the original Logger's associated class and the time at which the log call was made
                if (args.length > 0 && args[0] instanceof String) {
                    args[0] = String.format("[%s %s] %s",
                            logEntry.getCallingClass().getSimpleName(),
                            logEntry.getDateTime().format(DateTimeFormatter.ISO_DATE_TIME),
                            args[0]);
                }

                try {
                    method.invoke(LOG, args);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }

            LOG.info("===== FINISHED LOGGING THREAD PRINT =====");
        }

        // clear the log stack and active state for the current request thread
        logEntries.get().clear();
        active.set(false);
    }

    public static void setActive(boolean active) {
        ParsimusLoggingManager.active.set(active);
    }
}