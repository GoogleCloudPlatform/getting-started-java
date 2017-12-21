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
import com.google.appengine.api.datastore.KeyFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* Deletes an entry in Datastore for a particular key */
@SuppressWarnings("serial")
@WebServlet(name = "DeleteEntities", description = "Delete a blog post", value = "/delete")
public class DeleteEntities extends HttpServlet {

  DatastoreService datastore;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    PrintWriter out = resp.getWriter();

    // Get the websafe cursor and then convert it into a usable key
    // for retrieving a particular post

    Map<String, String[]> blogContent = req.getParameterMap();
    String[] postId = blogContent.get("id");
    String decodedKey =
        new String(Base64.getUrlDecoder().decode(postId[0])); // decode the websafe ID

    try {
      try {
        Entity deletePost = datastore.get(KeyFactory.stringToKey(decodedKey));
        // Delete the entity based on its key
        datastore.delete(deletePost.getKey());

        // Send the user to the confirmation page with personalised confirmation text
        String confirmation = "Post " + deletePost.getProperty("title") + " has been deleted.";

        req.setAttribute("confirmation", confirmation);
        req.getRequestDispatcher("/confirm.jsp").forward(req, resp);

      } catch (EntityNotFoundException e) {
        throw new ServletException("Datastore error", e);
      }
    } catch (DatastoreFailureException e) {
      throw new ServletException("Datastore error", e);
    }
  }

  @Override
  public void init() throws ServletException {

    // Setup datastore service
    datastore = DatastoreServiceFactory.getDatastoreService();
  }
}
