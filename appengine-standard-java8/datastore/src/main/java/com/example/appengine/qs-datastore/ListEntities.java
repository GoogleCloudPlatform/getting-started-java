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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* List entities from Datastore */
@SuppressWarnings("serial")
@WebServlet(name = "ListEntities", description = "List the latest news posts", urlPatterns = "/")
public class ListEntities extends HttpServlet {

  DatastoreService datastore;

  final Query q =
      new Query("Blogpost").setFilter(new FilterPredicate("title", FilterOperator.NOT_EQUAL, ""));

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    // Retrieve posts from Cloud Datastore and display them

    // Preformatted HTML
    String headers =
        "<!DOCTYPE html><meta charset=\"utf-8\"><h1>Welcome to the App Engine Blog</h1><h3><a href=\"form.jsp\">Add a new post</a></h3>";
    String blogPostDisplayFormat =
        "<h2> %s </h2> Posted at: %s by %s [<a href=\"/update?id=%s\">update</a>] | [<a href=\"/delete?id=%s\">delete</a>]<br><br> %s <br><br>";

    PrintWriter out = resp.getWriter();

    out.println(headers); // Print out HTML headers and page heading

    PreparedQuery pq = datastore.prepare(q);
    List<Entity> posts =
        pq.asList(FetchOptions.Builder.withLimit(5)); // Retrieve up to five entities

    posts.forEach(
        (result) -> {

          // Grab the key and convert it into a string in preperation for encoding
          String keyString = KeyFactory.keyToString(result.getKey());

          // Encode the entity's key with Base64
          String encodedID =
              new String(
                  Base64.getUrlEncoder().encodeToString(String.valueOf(keyString).getBytes()));

          // Build up string with values from the Datastore entity
          String recordOutput =
              String.format(
                  blogPostDisplayFormat,
                  result.getProperty("title"),
                  result.getProperty("timestamp"),
                  result.getProperty("author"),
                  encodedID,
                  encodedID,
                  result.getProperty("body"));

          out.println(recordOutput); // Print out the HTML
        });
  }

  @Override
  public void init() throws ServletException {

    // Setup Datastore connection
    datastore = DatastoreServiceFactory.getDatastoreService();
  }
}
