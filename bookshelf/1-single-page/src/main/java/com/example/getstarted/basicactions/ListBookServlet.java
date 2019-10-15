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

package com.example.getstarted.basicactions;

import com.example.getstarted.daos.BookDao;
import com.example.getstarted.daos.FirestoreDao;
import com.example.getstarted.objects.Book;
import com.example.getstarted.objects.Result;
import com.example.getstarted.util.CloudStorageHelper;
import com.google.common.base.Strings;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// [START bookshelf_list_books_servlet]
// a url pattern of "" makes this servlet the root servlet
@SuppressWarnings("serial")
@WebServlet(
    name = "list",
    urlPatterns = {"", "/books"},
    loadOnStartup = 1)
public class ListBookServlet extends HttpServlet {

  private static final Logger logger = Logger.getLogger(ListBookServlet.class.getName());

  @Override
  public void init() {
    BookDao dao = new FirestoreDao();

    CloudStorageHelper storageHelper = new CloudStorageHelper();
    this.getServletContext().setAttribute("dao", dao);
    this.getServletContext().setAttribute("storageHelper", storageHelper);
    // Hide upload when Cloud Storage is not
    this.getServletContext()
        .setAttribute(
            "isCloudStorageConfigured",
            // configured.
            !Strings.isNullOrEmpty(getServletContext().getInitParameter("bookshelf.bucket")));
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {
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
    StringBuilder bookNames = new StringBuilder();
    for (Book book : books) {
      bookNames.append(book.getTitle()).append(" ");
    }
    logger.log(Level.INFO, "Loaded books: " + bookNames.toString());
    req.setAttribute("cursor", endCursor);
    req.setAttribute("page", "list");
    req.getRequestDispatcher("/base.jsp").forward(req, resp);
  }
}
// [END bookshelf_list_books_servlet]
