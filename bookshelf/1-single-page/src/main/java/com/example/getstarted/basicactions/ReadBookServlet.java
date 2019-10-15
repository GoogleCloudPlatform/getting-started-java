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
import com.example.getstarted.objects.Book;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// [START bookshelf_read_servlet]
@SuppressWarnings("serial")
@WebServlet(
    name = "read",
    urlPatterns = {"/read"})
public class ReadBookServlet extends HttpServlet {

  private final Logger logger = Logger.getLogger(ReadBookServlet.class.getName());

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
    String id = req.getParameter("id");
    BookDao dao = (BookDao) this.getServletContext().getAttribute("dao");
    try {
      Book book = dao.readBook(id);
      logger.log(Level.INFO, "Read book with id {0}", id);
      req.setAttribute("book", book);
      req.setAttribute("page", "view");
      req.getRequestDispatcher("/base.jsp").forward(req, resp);
    } catch (Exception e) {
      throw new ServletException("Error reading book", e);
    }
  }
}
// [END bookshelf_read_servlet]
