package com.example.managedvms.gettingstartedjava.util;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

// TODO session filter, it takes all session variables from response and writes them to datastore
@WebFilter( "/*")
@SuppressWarnings("serial")
public class SessionFilter extends DatastoreHttpServlet implements Filter {

  private static final Logger logger = Logger.getLogger(SessionFilter.class.getName());
  // private ServletContext context;

  @Override
  public void init(FilterConfig config) throws ServletException {
    // TODO Auto-generated method stub
    // this.context = config.getServletContext();
  }

  @Override
  public void doFilter(ServletRequest servletReq, ServletResponse servletResp, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) servletReq;
    // check if the session cookies is there, if not there, make a session cookie
//    HttpServletResponse resp = (HttpServletResponse) servletResp;
    loadSessionVariables(req);
    chain.doFilter(servletReq, servletResp);
  }

  @Override
  public void destroy() {
    // TODO if the servlet is destroyed, should they log in again?
  }
}
