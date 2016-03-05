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
import com.example.managedvms.gettingstartedjava.util.CloudStorageHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

// [START example]
@SuppressWarnings("serial")
@MultipartConfig
@WebServlet(name = "create", value = "/create")
public class CreateBookServlet extends HttpServlet {

  private static final Logger logger = Logger.getLogger(CreateBookServlet.class.getName());

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {
    req.setAttribute("action", "Add");
    req.setAttribute("destination", "create");
    req.setAttribute("page", "form");
    req.getRequestDispatcher("/base.jsp").forward(req, resp);
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {
    CloudStorageHelper storageHelper =
        (CloudStorageHelper) req.getServletContext().getAttribute("storageHelper");
    String imageUrl = storageHelper.getImageUrl(req, resp, getServletContext().getInitParameter("bookshelf.bucket"));
    BookDao dao = (BookDao) this.getServletContext().getAttribute("dao");
    String createdByString = "";
    String createdByIdString = "";
    if (req.getAttribute("token") != null) {
      createdByString = (String) req.getAttribute("userEmail");
      createdByIdString = (String) req.getAttribute("userId");
    }
    Book book = new Book.Builder()
        .author(req.getParameter("author"))
        .createdBy(createdByString)
        .createdById(createdByIdString)
        .description(req.getParameter("description"))
        .publishedDate(req.getParameter("publishedDate"))
        .title(req.getParameter("title"))
        .imageUrl(imageUrl)
        .build();
    try {
      Long id = dao.createBook(book);
      logger.log(Level.INFO, "Created book {0}", book);
      resp.sendRedirect("/read?id=" + id.toString());
    } catch (Exception e) {
      throw new ServletException("Error creating book", e);
    }
  }
}
// [END example]
