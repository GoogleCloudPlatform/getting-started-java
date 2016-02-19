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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// [START example]
@WebServlet(name = "logout", value = "/logout")
@SuppressWarnings("serial")
public class LogoutServlet extends HttpServlet {

  private Logger logger = Logger.getLogger(this.getClass().getName());

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {
    // you can also make an authenticated request to logout
    Map<String, Cookie> cookieMap = new HashMap<>();
    Cookie[] cookies = req.getCookies();
    if (cookies != null) {
      for (Cookie c : cookies) {
        cookieMap.put(c.getName(), c);
      }
    }
    // TODO make this into a helper class
    try {
      if (cookieMap.containsKey("token")) {
        cookieMap.get("token").setMaxAge(0);
        resp.addCookie(cookieMap.get("token"));
        cookieMap.get("userEmail").setMaxAge(0);
        resp.addCookie(cookieMap.get("userEmail"));
        cookieMap.get("userImageUrl").setMaxAge(0);
        resp.addCookie(cookieMap.get("userImageUrl"));
        cookieMap.get("userId").setMaxAge(0);
        resp.addCookie(cookieMap.get("userId"));
      }
    } catch (NullPointerException e) {
      logger.log(Level.INFO, "The requested cookies are already null");
    }
    resp.sendRedirect("/books");
  }
}
// [END example]
