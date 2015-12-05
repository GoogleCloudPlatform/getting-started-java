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

package com.example.appengine.gettingstartedjava;

import com.example.appengine.gettingstartedjava.daos.BookDao;
import com.example.appengine.gettingstartedjava.daos.CloudSqlDao;
import com.example.appengine.gettingstartedjava.daos.DatastoreDao;
import com.example.appengine.gettingstartedjava.util.CloudStorageHelper;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class RootServlet extends HttpServlet {

  @Override
  public void init() throws ServletException {
    String storageType = System.getenv("AEV2_JAVA_STORAGETYPE");
    BookDao dao = null;
    switch (storageType) {
      case "datastore":
        dao = new DatastoreDao();
        break;
      case "cloudsql":
        try {
          dao = new CloudSqlDao();
        } catch (SQLException e) {
          throw new ServletException("SQL error", e);
        }
        break;
      default:
        throw new IllegalStateException("Invalid storage type. Check if environment variable is set.");
    }
    this.getServletContext().setAttribute("dao", dao);
    CloudStorageHelper storageHelper = new CloudStorageHelper();
    this.getServletContext().setAttribute("storageHelper", storageHelper);
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,
        ServletException {
    req.getRequestDispatcher("/books").forward(req, resp);
  }
}
