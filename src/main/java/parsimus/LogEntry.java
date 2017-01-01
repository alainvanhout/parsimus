package parsimus;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

public class LogEntry {

    private final LocalDateTime dateTime;
    private Class clazz;
    private Method method;
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
