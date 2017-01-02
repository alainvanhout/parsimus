package parsimus;

import org.slf4j.Logger;

import java.lang.reflect.Proxy;

/**
 * This class is not an a subclass of @Link org.slf4j.{@link org.slf4j.LoggerFactory} but does implement the same
 * LoggerFactory#getLogger(class) method. As such, it serves as a drop in replacement. Internally, it will use the
 * actual slf4j binding LoggerFactory to create a @Link {@link Logger} and will return a proxy to that object. That
 * proxy will add the log calls to the request thread full log stack, via @Link {@link ThreadLoggingManager}, before
 * passing the associated method on the actual slf4j Logger object.
 */
public class LoggerFactory {

    public static final ClassLoader CLASS_LOADER = LoggerInvocationHandler.class.getClassLoader();
    public static final Class[] INTERFACES = {Logger.class};

    public static Logger getLogger(Class clazz) {

        // a new Logger instance
        Logger logger = org.slf4j.LoggerFactory.getLogger(clazz);
        // a new proxy handler that knows which class the slf4j logger was made for
        LoggerInvocationHandler handler = new LoggerInvocationHandler(clazz, logger);
        // the actual proxy for the logger
        Logger proxy = (Logger) Proxy.newProxyInstance(CLASS_LOADER, INTERFACES, handler);

        return proxy;
    }
}
