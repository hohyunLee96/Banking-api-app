//package nl.inholland.bankingapi.filter;
//
//import jakarta.servlet.*;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.logging.Logger;
//
//@Component
//@Order(1)
//public class LargeRequestFilter implements Filter {
//    private final Logger logger = Logger.getLogger(this.getClass().getName());
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        Filter.super.init(filterConfig);
//    }
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        int size = servletRequest.getContentLength();
//        logger.info("Request size: " + size);
//        if (size > 100) {
//            logger.severe( "request with size " + size + " was rejected");
//            throw new ServletException("Request too large");
//        } else {
//            filterChain.doFilter(servletRequest, servletResponse); // pass on to the next filter
//        }
//    }
//
//    @Override
//    public void destroy() {
//        Filter.super.destroy();
//    }
//}
