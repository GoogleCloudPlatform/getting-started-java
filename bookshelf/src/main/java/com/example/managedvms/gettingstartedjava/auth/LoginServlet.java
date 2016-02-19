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

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.plus.PlusScopes;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// [START example]
@WebServlet(name = "login", value = "/login")
@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {
  private GoogleAuthorizationCodeFlow flow;
  private static final Collection<String> SCOPE =
      Arrays.asList(PlusScopes.USERINFO_EMAIL, PlusScopes.PLUS_LOGIN);
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();
  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {
    Map<String, Cookie> cookieMap = new HashMap<>();
    Cookie[] cookies = req.getCookies();
    for (Cookie c : cookies) {
      cookieMap.put(c.getName(), c);
    }
    flow =
        new GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT,
            JSON_FACTORY,
            System.getenv("CLIENT_ID"),
            System.getenv("CLIENT_SECRET"),
            SCOPE)
        .build();
    String state = new BigInteger(130, new SecureRandom()).toString(32);
    Cookie stateCookie = new Cookie("state", state);
    stateCookie.setPath("/");
    resp.addCookie(stateCookie);
    // do i need this, or do
    Cookie loginDestCookie = new Cookie("loginDestination", "");
    if(req.getAttribute("loginDestination") != null) {
      loginDestCookie.setValue((String) req.getAttribute("loginDestination"));
    } else {
      loginDestCookie.setValue("/books");
    }
    // Set the path to root so it's visible to all pages
    loginDestCookie.setPath("/");
    resp.addCookie(loginDestCookie);
    // callback url should be the one registered in Google Developers Console
    String url =
        flow.newAuthorizationUrl()
        .setRedirectUri(System.getenv("CALLBACK_URL"))
        .setState(state)
        .build();
    resp.sendRedirect(url);
  }
}
// [END example]
