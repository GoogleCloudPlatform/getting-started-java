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
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
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

/* Update an entry in Datastore for a particular key */
@SuppressWarnings("serial")
@WebServlet(name = "UpdateEntities", description = "Update a post", urlPatterns = "/update")
public class UpdateEntities extends HttpServlet {

  DatastoreService datastore;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    // Retrieve the blog post, display it in a HTML form

    // Pre-formatted HTML
    String outputFormat =
        "<!doctype html public \"-//w3c//dtd html 4.0 transitional//en\">\n<html>\n"
            + "<head><title>Updating a post</title></head><body>\n"
            + "<h2>Update Post</h2><form method=\"POST\" action=\"/update\"><div>"
            + "<label for=\"title\">Title</label>"
            + "<input type=\"text\" name=\"blogContent_title\" id=\"blogContent_title\" size=\"40\" value=\""
            + "%s\" class=\"form-control\" /></div><div><label for=\"description\">Post content</label>"
            + "<textarea name=\"blogContent_body\" id=\"blogContent_body\" rows=\"10\" cols=\"50\" class=\"form-control\">"
            + "%s</textarea></div><button type=\"submit\">Save</button>"
            + "<input type=\"hidden\" id=\"blogContent_id\" name=\"blogContent_id\" value=\"%s\">"
            + "<input type=\"hidden\" id=\"blogContent_author\" name=\"blogContent_author\" value=\""
            + "%s\"></form></body></html>";

    PrintWriter out = resp.getWriter();

    Map<String, String[]> blogContent = req.getParameterMap();
    String[] postId = blogContent.get("id"); // Grab the websafe ID
    String decodedKey =
        new String(Base64.getUrlDecoder().decode(postId[0])); // Decode the websafe ID

    try {
      Entity post = datastore.get(KeyFactory.stringToKey(decodedKey)); // Grab the post from Cloud Datastore

      // Display post
      out.println(
          String.format(
              outputFormat,
              post.getProperty("title"),
              post.getProperty("body"),
              postId[0],
              post.getProperty("author")));

    } catch (EntityNotFoundException e) {
      throw new ServletException("Datastore error", e);
    }
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    // Store the updated post in Datastore

    // Create a map of the httpParameters that we want and run it through jSoup
    Map<String, String> blogContent =
        req.getParameterMap()
            .entrySet()
            .stream()
            .filter(a -> a.getKey().startsWith("blogContent_"))
            .collect(
                Collectors.toMap(
                    p -> p.getKey(), p -> Jsoup.clean(p.getValue()[0], Whitelist.basic())));

    // Decode the websafe ID
    String decodedKey =
        new String(
            Base64.getUrlDecoder()
                .decode(blogContent.get("blogContent_id")));

    // Create a key from the decoded websafe string
    Key originalPostKey = KeyFactory.stringToKey(decodedKey);

    // Create a new entity with the same key as the original
    Entity post = new Entity("Blogpost", originalPostKey.getId());

    // Populate the new entity with the updated blog post contents
    post.setProperty("title", blogContent.get("blogContent_title"));
    post.setProperty("author", blogContent.get("blogContent_author"));
    post.setProperty("body", blogContent.get("blogContent_body"));
    post.setProperty("timestamp", new Date());

    try {
      datastore.put(post); // Store the post

      // Send the user to the confirmation page with personalised confirmation text
      String confirmation =
          "Post with title " + blogContent.get("blogContent_title") + " updated.";

      req.setAttribute("confirmation", confirmation);
      req.getRequestDispatcher("/confirm.jsp").forward(req, resp);
    } catch (DatastoreFailureException e) {
      throw new ServletException("Datastore error", e);
    }
  }

  @Override
  public void init() throws ServletException {

    // Create a connection to Cloud Datastore
    datastore = DatastoreServiceFactory.getDatastoreService();
  }
}
