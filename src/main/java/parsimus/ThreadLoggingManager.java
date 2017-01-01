package parsimus;

import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ThreadLoggingManager {

    private static final Logger LOG = LoggerFactory.getLogger(ThreadLoggingManager.class);

    private static ThreadLocal<List<LogEntry>> logEntries = new ThreadLocal<>();
    private static ThreadLocal<Boolean> active = new ThreadLocal<>();

    public static void init() {
        logEntries.set(new ArrayList<>());
        active.set(false);
    }

    public static void add(Class clazz, Method method, Object[] args) {
        List<LogEntry> entries = ThreadLoggingManager.logEntries.get();

        entries.add(new LogEntry(clazz, method, args, LocalDateTime.now()));
    }

    public static void printAll() {
        if (!active.get()) {
            logEntries.get().clear();
            return;
        }

        List<LogEntry> entries = new ArrayList<>(ThreadLoggingManager.logEntries.get());

        LOG.info("===== STARTING THREAD LOGGING PRINT =====");

        for (LogEntry logEntry : entries) {

            Method method = logEntry.getMethod();
            Object[] args = logEntry.getArgs();

            if (args.length > 0 && args[0] instanceof String) {
                args[0] = String.format("[%s %s] %s",
                        logEntry.getCallingClass().getSimpleName(),
                        logEntry.getDateTime().format(DateTimeFormatter.ISO_TIME),
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

    public static void setActive(boolean active) {
        ThreadLoggingManager.active.set(active);
    }
}