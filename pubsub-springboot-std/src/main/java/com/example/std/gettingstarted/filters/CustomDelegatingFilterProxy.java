package com.example.std.gettingstarted.filters;

import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class CustomDelegatingFilterProxy extends DelegatingFilterProxy
{


    @Override
    public void doFilter(ServletRequest req,
    		         ServletResponse resp,
    			 FilterChain chain)
    		throws IOException, ServletException
    {
        chain.doFilter( req, new HttpServletResponseWithGetStatus(resp));
    }
}
