/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.getstarted.basicactions;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet to respond to health checks.
 *
 * <p>This servlet responds to all requests with the message "ok" and HTTP code
 * 200. It is periodically run by the health checker to determine if the server
 * is still responding to requests.
 *
 * <p>A health check servlet like this is required when using the load balancer
 * for Google Compute Engine, but App Engine flexible environment provides one
 * for you if you do not supply one.
 */
@SuppressWarnings("serial")
@WebServlet(
    name = "health",
    urlPatterns = {
      "/_ah/health", // App Engine flexible environment sends a request to
                     // the path '/_ah/health' periodically to check if an
                     // instance is still serving requests.
                     //
                     // For Google Compute Engine, we configure the load
                     // balancer health checker to use this path.

      "/_ah/start",  // App Engine flexible environment sends a request to the
                     // path '/_ah/start' when an instance starts up. This can
                     // be useful for warming up resources like in-memory
                     // caches and database connection pools.
                     //
                     // To acheive a similar result with Google Compute Engine,
                     // a startup script
                     //     https://g.co/cloud/compute/docs/startupscript
                     // or an init() method on a servlet with loadOnStartup=1
                     // can be used. See ListBookServlet for an example of
                     // this.

      "/_ah/stop"})  // App Engine flexible environment sends a request to the
                     // path '/_ah/stop' when an instance is about to be shut
                     // down.  This can be useful for cleaning up resources and
                     // finishing data transactions.
                     //
                     // To acheive a similar result with Google Compute Engine,
                     // a shutdown script
                     //     https://g.co/cloud/compute/docs/shutdownscript
                     // can be used.
public class HealthServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.setContentType("text/plain");
    PrintWriter out = resp.getWriter();
    out.println("ok");
  }
}
