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
package com.example.appengine.datastore;

import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;


/* Create an entry in Datastore */
@SuppressWarnings("serial")
@WebServlet(name = "WriteEntities", description = "Create a Datastore entity", urlPatterns = "/create")
public class WriteEntities extends HttpServlet {

  DatastoreService datastore;

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    // Store a post in Datastore

    // Create a map of the httpParameters that we want and run it through jSoup
    Map<String, String> blogContent =
        req.getParameterMap()
            .entrySet()
            .stream()
            .filter(a -> a.getKey().startsWith("blogContent_"))
            .collect(
                Collectors.toMap(
                    p -> p.getKey(), p -> Jsoup.clean(p.getValue()[0], Whitelist.basic())));

    Entity post = new Entity("Blogpost"); // Create a new entity

    post.setProperty("title", blogContent.get("blogContent_title"));
    post.setProperty("author", blogContent.get("blogContent_author"));
    post.setProperty("body", blogContent.get("blogContent_description"));
    post.setProperty("timestamp", new Date());

    try {
      datastore.put(post); // Store the entity

      // Send the user to the confirmation page with personalised confirmation text
      String confirmation = "Post with title " + blogContent.get("blogContent_title") + " created.";

      req.setAttribute("confirmation", confirmation);
      req.getRequestDispatcher("/confirm.jsp").forward(req, resp);
    } catch (DatastoreFailureException e) {
      throw new ServletException("Datastore error", e);
    }
  }

  @Override
  public void init() throws ServletException {

    // Datastore connection
    datastore = DatastoreServiceFactory.getDatastoreService();
  }
}
