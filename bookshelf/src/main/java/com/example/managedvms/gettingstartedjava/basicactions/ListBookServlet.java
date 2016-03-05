/**
 * Copyright 2015 Google Inc. All Rights Reserved.
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

package com.example.managedvms.gettingstartedjava.basicactions;

import com.example.managedvms.gettingstartedjava.daos.BookDao;
import com.example.managedvms.gettingstartedjava.daos.CloudSqlDao;
import com.example.managedvms.gettingstartedjava.daos.DatastoreDao;
import com.example.managedvms.gettingstartedjava.objects.Book;
import com.example.managedvms.gettingstartedjava.objects.Result;
import com.example.managedvms.gettingstartedjava.util.CloudStorageHelper;
import com.example.managedvms.gettingstartedjava.util.DatastoreHttpServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// [START example]
// a url pattern of "" makes this servlet the root servlet
@WebServlet(name = "list", urlPatterns = {"", "/books" }, loadOnStartup = 1)
@SuppressWarnings("serial")
public class ListBookServlet extends DatastoreHttpServlet {

  private static final Logger logger = Logger.getLogger(ListBookServlet.class.getName());

  /**
   * Initializes the storage DAO and creates a session identification cookie.
   */
  @Override
  public void init() throws ServletException {
    // Creates the DAO based on the environment variable
    String storageType = getServletContext().getInitParameter("bookshelf.storageType");

    BookDao dao = null;
    switch (storageType) {
      case "datastore":
        dao = new DatastoreDao();
        break;
      case "cloudsql":
        try {
          dao = new CloudSqlDao(getServletContext().getInitParameter("sql.url"));
        } catch (SQLException e) {
          throw new ServletException("SQL error", e);
        }
        break;
      default:
        throw new IllegalStateException(
            "Invalid storage type. Check if bookshelf.storageType property is set.");
    }
    this.getServletContext().setAttribute("dao", dao);
    CloudStorageHelper storageHelper = new CloudStorageHelper();
    this.getServletContext().setAttribute("storageHelper", storageHelper);

  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,
        ServletException {
    BookDao dao = (BookDao) this.getServletContext().getAttribute("dao");
    String startCursor = req.getParameter("cursor");
    List<Book> books = null;
    String endCursor = null;
    try {
      Result<Book> result = dao.listBooks(startCursor);
      logger.log(Level.INFO, "Retrieved list of all books");
      books = result.result;
      endCursor = result.cursor;
    } catch (Exception e) {
      throw new ServletException("Error listing books", e);
    }
    req.getSession().getServletContext().setAttribute("books", books);
    req.setAttribute("cursor", endCursor);
    req.setAttribute("page", "list");
    loadSessionVariables(req);
    req.getRequestDispatcher("/base.jsp").forward(req, resp);
  }
}
// [END example]
