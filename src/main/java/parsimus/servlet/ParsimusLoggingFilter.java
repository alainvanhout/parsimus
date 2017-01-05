package parsimus.servlet;

import org.slf4j.Logger;
import parsimus.LoggerFactory;
import parsimus.ParsimusLoggingManager;

import javax.servlet.*;
import java.io.IOException;

/**
 * Servlet filter which will print the full log stack of the request thread, if @Link {@link ParsimusLoggingManager#active}
 * was set to true inside that request thread.
 *
 * Setting @Link ParsimusLoggingFilter#activeOnException to true will cause the filter itself to catch any exceptions,
 * log them, and set @Link {@link ParsimusLoggingManager#active} to true, print the full log stack and do a (wrapped) rethrow
 * the exception. This static field can be set directly, or can be specified via the system property 'parsimus.activeOnException'.
 */
public class ParsimusLoggingFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(ParsimusLoggingFilter.class);
    private static final Logger PLAIN_LOG = org.slf4j.LoggerFactory.getLogger(ParsimusLoggingFilter.class);

    public static final String PARSIMUS_ACTIVE_ON_EXCEPTION = "parsimus.activeOnException";

    private static boolean activeOnException = false;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        ParsimusLoggingManager.reset();

        if (!activeOnException){
            // simple handling (separated out to avoid the necessity for exception wrapping)
            filterChain.doFilter(servletRequest, servletResponse);
            ParsimusLoggingManager.printAll();
        } else {
            // catch any exceptions, log them and active ThreadLoggingManager for this request thread
            Exception exception = null;
            try {
                filterChain.doFilter(servletRequest, servletResponse);
            } catch (Exception e) {
                exception = e;
                LOG.error("Encountered unexpected exception", exception);
                ParsimusLoggingManager.setActive(true);
            }
            ParsimusLoggingManager.printAll();

            if (exception != null) {
                throw new ServletException(exception);
            }
        }
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
