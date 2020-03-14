/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.getstarted.auth;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter(filterName = "ListByUserFilter", value = "/books/mine")
public class ListByUserFilter implements Filter {
  // [START createLogger]
  private static final Logger logger = Logger.getLogger(ListByUserFilter.class.getName());
  // [END createLogger]

  @Override
  public void init(FilterConfig config) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest servletReq, ServletResponse servletResp, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) servletReq;
    HttpServletResponse resp = (HttpServletResponse) servletResp;

    // [START logStuff]
    String instanceId =
        System.getenv().containsKey("GAE_MODULE_INSTANCE")
            ? System.getenv("GAE_MODULE_INSTANCE") : "-1";
    logger.log(
        Level.INFO,
        "ListByUserFilter processing new request for path: " + req.getRequestURI()
            + " and instance: " + instanceId);
    // [END logStuff]

    if (req.getSession().getAttribute("token") == null
        && req.getSession().getAttribute("state") == null) {
      logger.log(Level.INFO, "token not detected, setting loginDestination to /books/mine");
      req.setAttribute("loginDestination", "/books/mine");
      resp.sendRedirect("/login");
    } else {
      chain.doFilter(servletReq, servletResp);
    }
  }

  @Override
  public void destroy() {
    logger.log(Level.INFO, "ListByUserFilter is de-initializing");
  }
}
