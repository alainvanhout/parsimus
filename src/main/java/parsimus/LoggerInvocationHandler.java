package parsimus;

import org.slf4j.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class LoggerInvocationHandler implements InvocationHandler {

    private final Class clazz;
    private final Logger logger;

    public LoggerInvocationHandler(Class clazz, Logger logger) {
        this.clazz = clazz;
        this.logger = logger;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ThreadLoggingManager.add(clazz, method, args);

        return method.invoke(logger, args);
    }
}