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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Retrieves and displays blog posts stored in Cloud Datastore
 */

@SuppressWarnings("serial")
@WebServlet(name = "ListEntities", description = "List the latest news posts", urlPatterns = "/")
public class ListEntities extends HttpServlet {

  DatastoreService datastore;

  // Query for Datastore - returns all posts that do not have a blank title
  final Query q =
      new Query("Blogpost").setFilter(new FilterPredicate("title", FilterOperator.NOT_EQUAL, ""));

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    PreparedQuery pq = datastore.prepare(q);
    QueryResultList<Entity> posts = pq.asQueryResultList(FetchOptions.Builder.withLimit(5)); // Fetch five entries

    PrintWriter out = resp.getWriter();

    out.println(
        "<!DOCTYPE html><meta charset=\"utf-8\"><h1>Welcome to the App Engine Blog</h1><h3><a href=\"form.jsp\">Add a new post</a></h3>");

    // Print out each post, adding a link for updating an deleting the post
    posts.forEach(
        (result) -> {
          String safeCursor = posts.getCursor().toWebSafeString();
          
          out.println(
              "<h2>" + result.getProperty("title") + "</h2>"
                  + "Posted at: " + result.getProperty("timestamp") + " by " + result.getProperty("author")
                  + " [<a href=\"/update?id=" + safeCursor + "\">update</a>] | "
                  + "[<a href=\"/delete?id=" + safeCursor + "\">delete</a>]<br><br>"
                  + result.getProperty("body")
                  + "<br><br>");
        });
  }

  @Override
  public void init() throws ServletException {

    // Setup datastore connection
    datastore = DatastoreServiceFactory.getDatastoreService();
  }
}
