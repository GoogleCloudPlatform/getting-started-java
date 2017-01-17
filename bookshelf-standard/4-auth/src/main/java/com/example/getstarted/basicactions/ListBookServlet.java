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

import com.example.getstarted.daos.BookDao;
import com.example.getstarted.daos.CloudSqlDao;
import com.example.getstarted.daos.DatastoreDao;
import com.example.getstarted.objects.Book;
import com.example.getstarted.objects.Result;
import com.example.getstarted.util.CloudStorageHelper;

import com.google.common.base.Strings;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// [START example]
@SuppressWarnings("serial")
public class ListBookServlet extends HttpServlet {

  @Override
  public void init() throws ServletException {
    BookDao dao = null;

    CloudStorageHelper storageHelper = new CloudStorageHelper();

    // Creates the DAO based on the Context Parameters
    String storageType = this.getServletContext().getInitParameter("bookshelf.storageType");
    switch (storageType) {
      case "datastore":
        dao = new DatastoreDao();
        break;
      case "cloudsql":
        try {
          // Use this url when using dev appserver, but connecting to Cloud SQL
          String connect = this.getServletContext().getInitParameter("sql.urlRemote");
          if (connect.contains("localhost")) {
            // Use this url when using a local mysql server
            connect = this.getServletContext().getInitParameter("sql.urlLocal");
          } else if (System.getProperty("com.google.appengine.runtime.version")
              .startsWith("Google App Engine/")) {
            // Use this url when on App Engine, connecting to Cloud SQL.
            // Uses a special adapter because of the App Engine sandbox.
            connect = this.getServletContext().getInitParameter("sql.urlRemoteGAE");
          }
          dao = new CloudSqlDao(connect);
        } catch (SQLException e) {
          throw new ServletException("SQL error", e);
        }
        break;
      default:
        throw new IllegalStateException(
            "Invalid storage type. Check if bookshelf.storageType property is set.");
    }
    this.getServletContext().setAttribute("dao", dao);
    this.getServletContext().setAttribute("storageHelper", storageHelper);
    this.getServletContext().setAttribute(
        "isCloudStorageConfigured",  // Hide upload when Cloud Storage is not configured.
        !Strings.isNullOrEmpty(getServletContext().getInitParameter("bookshelf.bucket")));
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
      books = result.result;
      endCursor = result.cursor;
    } catch (Exception e) {
      throw new ServletException("Error listing books", e);
    }
    req.getSession().getServletContext().setAttribute("books", books);
    StringBuilder bookNames = new StringBuilder();
    for (Book book : books) {
      bookNames.append(book.getTitle() + " ");
    }
    req.setAttribute("cursor", endCursor);
    req.setAttribute("page", "list");
    req.getRequestDispatcher("/base.jsp").forward(req, resp);
  }
}
// [END example]
