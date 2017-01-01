package parsimus.servlet;

import parsimus.ThreadLoggingManager;

import javax.servlet.*;
import java.io.IOException;

public class ParsimusLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        ThreadLoggingManager.init();
        filterChain.doFilter(servletRequest, servletResponse);
        ThreadLoggingManager.printAll();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}
