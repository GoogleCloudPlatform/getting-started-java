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
import com.example.managedvms.gettingstartedjava.objects.Book;
import com.example.managedvms.gettingstartedjava.objects.Result;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// [START example]
@SuppressWarnings("serial")
@WebServlet(name = "listbyuser", value = "/books/mine")
public class ListByUserServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,
        ServletException {
    Map<String, Cookie> cookieMap = new HashMap<>();
    Cookie[] cookies = req.getCookies();
    if (cookies != null) {
      for (Cookie c : cookies) {
        cookieMap.put(c.getName(), c);
      }
    }
    if (!cookieMap.containsKey("token")) {
      req.setAttribute("loginDestination", "/books/mine");
      req.getRequestDispatcher("/login").forward(req, resp);
      return;
    }
    BookDao dao = (BookDao) this.getServletContext().getAttribute("dao");
    String startCursor = req.getParameter("cursor");
    List<Book> books = null;
    String endCursor = null;
    try {
      Result<Book> result = dao.listBooksByUser(
          cookieMap.get("userId").getValue(), startCursor);
      books = result.result;
      endCursor = result.cursor;
    } catch (Exception e) {
      throw new ServletException("Error listing books", e);
    }
    req.getSession().getServletContext().setAttribute("books", books);
    req.setAttribute("cursor", endCursor);
    req.setAttribute("page", "list");
    req.getRequestDispatcher("/base.jsp").forward(req, resp);
  }
}
// [END example]
