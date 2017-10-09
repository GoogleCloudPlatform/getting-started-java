/**
 * Copyright 2017 Google Inc.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.appengine.helloworld;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

@SuppressWarnings("serial")
@WebServlet(value = "/create")
public class PostServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    PrintWriter out = resp.getWriter();

    // Create a map of the httpParameters that we want and run it through jSoup
    Map<String, String> blogContent =
        req.getParameterMap()
            .entrySet()
            .stream()
            .filter(a -> a.getKey().startsWith("blogContent_"))
            .collect(
                Collectors.toMap(
                    p -> p.getKey(), p -> Jsoup.clean(p.getValue()[0], Whitelist.basic())));

    out.println(
        "Article with the title: "
            + req.getParameter("blogContent_title")
            + " by "
            + req.getParameter("blogContent_author")
            + " and the content: "
            + req.getParameter("blogContent_description")
            + " added.");
  }
}
