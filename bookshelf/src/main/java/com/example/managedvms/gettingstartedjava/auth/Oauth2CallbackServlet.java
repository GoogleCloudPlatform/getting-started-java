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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.plus.PlusScopes;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
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
@WebServlet(name = "oauth2callback", value = "/oauth2callback")
@SuppressWarnings("serial")
public class Oauth2CallbackServlet extends HttpServlet {

  private Logger logger = Logger.getLogger(this.getClass().getName());

  private GoogleAuthorizationCodeFlow flow;
  private static final Collection<String> SCOPE =
      Arrays.asList(PlusScopes.USERINFO_EMAIL, PlusScopes.PLUS_LOGIN);
  private static final String LOGIN_API_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();
  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,
      ServletException {
    Map<String, Cookie> cookieMap = new HashMap<>();
    Cookie[] cookies = req.getCookies();
    for (Cookie c : cookies) {
      logger.log(Level.INFO, c.getName() + " " + c.getValue());
      cookieMap.put(c.getName(), c);
    }
    // Ensure that this is no request forgery going on, and that the user
    // sending us this connect request is the user that was supposed to.
    logger.log(Level.INFO, cookieMap.get("state").getValue());
    logger.log(Level.INFO, req.getParameter("state"));
    if (!req.getParameter("state")
        .equals(cookieMap.get("state").getValue())) {
      resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      resp.getWriter().print("Invalid state parameter.");
      return;
    }
    // remove one-time use state
    cookieMap.get("state").setMaxAge(0);
    resp.addCookie(cookieMap.get("state"));

    flow =
        new GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT,
            JSON_FACTORY,
            System.getenv("CLIENT_ID"),
            System.getenv("CLIENT_SECRET"),
            SCOPE)
        .build();
    final TokenResponse tokenResponse =
        flow.newTokenRequest(req.getParameter("code"))
        .setRedirectUri(System.getenv("CALLBACK_URL"))
        .execute();

    // keep track of the token
    resp.addCookie(new Cookie("token", tokenResponse.toString()));
    final Credential credential = flow.createAndStoreCredential(tokenResponse, null);
    final HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);
    // Make an authenticated request
    final GenericUrl url = new GenericUrl(LOGIN_API_URL);
    final HttpRequest request = requestFactory.buildGetRequest(url);
    request.getHeaders().setContentType("application/json");

    final String jsonIdentity = request.execute().parseAsString();
    @SuppressWarnings("unchecked")
    HashMap<String, String> userIdResult =
        new ObjectMapper().readValue(jsonIdentity, HashMap.class);
    // from this map, extract the relevant profile info and store it in the session
    resp.addCookie(new Cookie("userEmail", userIdResult.get("email")));
    resp.addCookie(new Cookie("userId", userIdResult.get("id")));
    resp.addCookie(new Cookie("userImageUrl", userIdResult.get("picture")));
    resp.sendRedirect(cookieMap.get("loginDestination").getValue());
  }
}
// [END example]
