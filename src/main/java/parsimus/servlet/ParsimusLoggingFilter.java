package parsimus.servlet;

import org.slf4j.Logger;
import parsimus.LoggerFactory;
import parsimus.Parsimus;
import parsimus.ParsimusLoggingManager;

import javax.servlet.*;
import java.io.IOException;

/**
 * Servlet filter which will print the full log stack of the request thread, if @Link {@link ParsimusLoggingFilter#active}
 * was set to true inside that request thread.
 *
 * Setting @Link ParsimusLoggingFilter#activeOnException to true will cause the filter itself to catch any exceptions,
 * log them, and set @Link {@link ParsimusLoggingFilter#active} to true, print the full log stack and do a (wrapped) rethrow
 * the exception. This static field can be set directly, or can be specified via the system property 'parsimus.activeOnException'.
 */
public class ParsimusLoggingFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(Parsimus.class);

    private static final Logger PLAIN_LOG = org.slf4j.LoggerFactory.getLogger(ParsimusLoggingFilter.class);

    public static final String PARSIMUS_ACTIVE_ON_EXCEPTION = "parsimus.activeOnException";

    private static boolean activeOnException = false;
    /**
     * Flag which determines whether the full log stack of the current request thread should be printed once the request
     * thread has passed the filter chaining in @{@link ParsimusLoggingFilter}
     */
    public static ThreadLocal<Boolean> active = new ThreadLocal<>();

    public static void setActive(boolean active) {
        ParsimusLoggingFilter.active.set(active);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        ParsimusLoggingManager.reset();
        active.set(false);

        if (!activeOnException){
            // simple handling (separated out to avoid the necessity for exception wrapping)
            filterChain.doFilter(servletRequest, servletResponse);
            print();
        } else {
            // catch any exceptions, log them and active ThreadLoggingManager for this request thread
            Exception exception = doFilterAndCatch(servletRequest, servletResponse, filterChain);
            print();

            if (exception != null) {
                throw new ServletException(exception);
            }
        }
    }

    private void print() {
        ParsimusLoggingManager.print();
        active.set(false);
    }

    private Exception doFilterAndCatch(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        Exception exception = null;
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            exception = e;
            LOG.error("Encountered unexpected exception", exception);
            setActive(true);
        }
        return exception;
    }

    public static void setActiveOnException(boolean activeOnException) {
        ParsimusLoggingFilter.activeOnException = activeOnException;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (Boolean.parseBoolean(System.getProperty(PARSIMUS_ACTIVE_ON_EXCEPTION))) {
            activeOnException = true;
            PLAIN_LOG.info("Parsimus activeOnException was enabled via system property");
        }
    }

    @Override
    public void destroy() {
    }
}
