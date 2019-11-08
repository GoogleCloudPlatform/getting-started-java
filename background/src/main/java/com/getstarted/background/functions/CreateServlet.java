/* Copyright 2019 Google LLC
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

package com.getstarted.background.functions;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet for the Translation Request form. */
@WebServlet(
    name = "create",
    urlPatterns = {"/create"})
public class CreateServlet extends HttpServlet {
  private static Logger logger = Logger.getLogger(CreateServlet.class.getName());

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.setAttribute("action", "Add");
    req.setAttribute("destination", "create");
    req.setAttribute("page", "form");
    req.getRequestDispatcher("/base.jsp").forward(req, resp);
  }

  // [START getting_started_background_app_request]
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String text = req.getParameter("data");
    String sourceLang = req.getParameter("sourceLang");
    String targetLang = req.getParameter("targetLang");

    Enumeration<String> paramNames = req.getParameterNames();
    while (paramNames.hasMoreElements()) {
      String paramName = paramNames.nextElement();
      logger.warning("Param name: " + paramName + " = " + req.getParameter(paramName));
    }

    Publisher publisher = (Publisher) getServletContext().getAttribute("publisher");

    PubsubMessage pubsubMessage =
        PubsubMessage.newBuilder()
            .setData(ByteString.copyFromUtf8(text))
            .putAttributes("sourceLang", sourceLang)
            .putAttributes("targetLang", targetLang)
            .build();

    try {
      publisher.publish(pubsubMessage).get();
    } catch (InterruptedException | ExecutionException e) {
      throw new ServletException("Exception publishing message to topic.", e);
    }

    resp.sendRedirect("/");
  }
  // [END getting_started_background_app_request]
}
