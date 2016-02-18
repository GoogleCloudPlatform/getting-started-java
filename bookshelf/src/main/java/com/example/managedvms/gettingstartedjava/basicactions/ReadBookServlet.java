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
import com.example.managedvms.gettingstartedjava.util.DatastoreHttpServlet;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// [START example]
@SuppressWarnings("serial")
@WebServlet(name = "read", value = "/read")
public class ReadBookServlet extends DatastoreHttpServlet {

  private final Logger logger =
      Logger.getLogger(
         com.example.managedvms.gettingstartedjava.basicactions.ReadBookServlet.class.getName());

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,
      ServletException {
    Long id = Long.decode(req.getParameter("id"));
    BookDao dao = (BookDao) this.getServletContext().getAttribute("dao");
    try {
      Book book = dao.readBook(id);
      logger.log(Level.INFO, "Read book with id {0}", id);
      req.setAttribute("book", book);
      req.setAttribute("page", "view");
      loadSessionVariables(req);
      req.getRequestDispatcher("/base.jsp").forward(req, resp);
    } catch (Exception e) {
      throw new ServletException("Error reading book", e);
    }
  }
}
// [END example]
