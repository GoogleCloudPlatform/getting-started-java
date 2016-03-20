package com.example.managedvms.gettingstartedjava.auth;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

// [START init]
@WebFilter(filterName = "LogoutFilter",
    urlPatterns = { "/logout" })
public class LogoutFilter implements Filter {

  private static final Logger logger = Logger.getLogger(ListByUserFilter.class.getName());
// [END init]
  @Override
  public void init(FilterConfig config) throws ServletException { }

  @Override
  public void doFilter(ServletRequest servletReq, ServletResponse servletResp, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) servletReq;
    HttpServletResponse resp = (HttpServletResponse) servletResp;
    String path = req.getRequestURI();

    chain.doFilter(servletReq, servletResp);

    if (path.startsWith("/logout")) {
      resp.sendRedirect("/books");
    }
  }

  @Override
  public void destroy() {
    logger.log(Level.INFO, "destroy called in LogoutFilter");
  }
}
