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

package com.example.appengine.gettingstartedjava.servlets;

import com.example.appengine.gettingstartedjava.daos.BookDao;
import com.example.appengine.gettingstartedjava.objects.Book;
import com.example.appengine.gettingstartedjava.util.CloudStorageHelper;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@MultipartConfig
@WebServlet(name = "update", value = "/update")
public class UpdateBookServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {
    BookDao dao = (BookDao) this.getServletContext().getAttribute("dao");
    try {
      Book book = dao.readBook(Long.decode(req.getParameter("id")));
      req.setAttribute("book", book);
      req.setAttribute("action", "Edit");
      req.setAttribute("destination", "update");
      req.getRequestDispatcher("/form.jsp").forward(req, resp);
    } catch (Exception e) {
      throw new ServletException("Error loading book for editing", e);
    }
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {
    CloudStorageHelper storageHelper =
        (CloudStorageHelper) req.getServletContext().getAttribute("storageHelper");
    String imageUrl = storageHelper.getImageUrl(req, resp);
    BookDao dao = (BookDao) this.getServletContext().getAttribute("dao");
    Book book = new Book.Builder()
        .author(req.getParameter("author"))
        .description(req.getParameter("description"))
        .id(Long.decode(req.getParameter("id")))
        .publishedDate(req.getParameter("publishedDate"))
        .title(req.getParameter("title"))
        .imageUrl(imageUrl)
        .build();
    try {
      dao.updateBook(book);
      resp.sendRedirect("/read?id=" + req.getParameter("id"));
    } catch (Exception e) {
      throw new ServletException("Error updating book", e);
    }
  }
}
