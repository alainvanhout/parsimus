package parsimus;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * A simple POJO which contains the information related to a single logging call that was added to the full log stack
 * before being sent on to the actual slf4j logger.
 */
public class LogEntry {

    /**
     * The DateTime at which the log call was made
     */
    private final LocalDateTime dateTime;
    /**
     * The class that the slf4j logger and its parsimus proxy which made this log call refer to
     */
    private Class clazz;
    /**
     * The slf4j logger method that was called on the proxy
     */
    private Method method;
    /**
     * The arguments that were passed to the method call
     */
    private Object[] args;

    public LogEntry(Class clazz, Method method, Object[] args, LocalDateTime dateTime) {
        this.clazz = clazz;
        this.method = method;
        this.args = args;
        this.dateTime = dateTime;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArgs() {
        return args;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Class getCallingClass() {
        return clazz;
    }
}
