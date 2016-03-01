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

package com.example.managedvms.gettingstartedjava.auth;

import com.example.managedvms.gettingstartedjava.util.DatastoreHttpServlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// [START example]
@WebServlet(name = "logout", value = "/logout")
@SuppressWarnings("serial")
public class LogoutServlet extends DatastoreHttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {
    // you can also make an authenticated request to logout, but here we choose to
    // simply delete the session variables for simplicity
    deleteSession(getCookieValue(req, "bookshelfSessionId"));
    resp.sendRedirect("/books");
  }
}
// [END example]
