package nl.inholland.bankingapi.filter;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LargeRequestFilter implements Filter {
    @Value("${application.max.content.size}")
    private int maxContentSize;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException, java.io.IOException {
        int size = servletRequest.getContentLength();
        if (size > maxContentSize) {
            throw new ServletException("Request too large");
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}