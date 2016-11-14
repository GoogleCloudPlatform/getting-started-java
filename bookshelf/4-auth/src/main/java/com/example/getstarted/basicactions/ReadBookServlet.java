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
import com.example.getstarted.objects.Book;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// [START example]
@SuppressWarnings("serial")
@WebServlet(name = "read", value = "/read")
public class ReadBookServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,
      ServletException {
    Long id = Long.decode(req.getParameter("id"));
    BookDao dao = (BookDao) this.getServletContext().getAttribute("dao");
    try {
      Book book = dao.readBook(id);
      req.setAttribute("book", book);
      req.setAttribute("page", "view");
      req.getRequestDispatcher("/base.jsp").forward(req, resp);
    } catch (Exception e) {
      throw new ServletException("Error reading book", e);
    }
  }
}
// [END example]
