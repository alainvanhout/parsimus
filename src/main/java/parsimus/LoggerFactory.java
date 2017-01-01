package parsimus;

import org.slf4j.Logger;

import java.lang.reflect.Proxy;

public class LoggerFactory {

    private static final ThreadLocal<LoggerFactory> threadLogger = new ThreadLocal<>();

    private static ThreadLocal<Logger> logger = new ThreadLocal<>();

    public static Logger getLogger(Class clazz) {

        logger.set(org.slf4j.LoggerFactory.getLogger(clazz));

        if (threadLogger.get() == null) {
            threadLogger.set(new LoggerFactory());
        }

        LoggerInvocationHandler handler = new LoggerInvocationHandler(clazz, logger.get());

        Logger proxy = (Logger) Proxy.newProxyInstance(
                LoggerInvocationHandler.class.getClassLoader(),
                new Class[]{Logger.class}, handler);

        return proxy;
    }
}
