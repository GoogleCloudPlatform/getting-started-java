/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.getstarted.auth;

import com.fasterxml.jackson.databind.ObjectMapper;

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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// [START example]
@WebServlet(name = "oauth2callback", value = "/oauth2callback")
@SuppressWarnings("serial")
public class Oauth2CallbackServlet extends HttpServlet {

  private static final Logger logger = Logger.getLogger(Oauth2CallbackServlet.class.getName());
  private static final Collection<String> SCOPES = Arrays.asList("email", "profile");
  private static final String USERINFO_ENDPOINT
      = "https://www.googleapis.com/plus/v1/people/me/openIdConnect";
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();
  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

  private GoogleAuthorizationCodeFlow flow;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,
      ServletException {

    // Ensure that this is no request forgery going on, and that the user
    // sending us this connect request is the user that was supposed to.
    if (req.getSession().getAttribute("state") == null
        || !req.getParameter("state").equals((String) req.getSession().getAttribute("state"))) {
      resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      logger.log(
          Level.WARNING,
          "Invalid state parameter, expected " + (String) req.getSession().getAttribute("state")
              + " got " + req.getParameter("state"));
      resp.sendRedirect("/books");
      return;
    }

    req.getSession().removeAttribute("state");     // Remove one-time use state.

    flow = new GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT,
        JSON_FACTORY,
        getServletContext().getInitParameter("bookshelf.clientID"),
        getServletContext().getInitParameter("bookshelf.clientSecret"),
        SCOPES).build();

    final TokenResponse tokenResponse =
        flow.newTokenRequest(req.getParameter("code"))
            .setRedirectUri(getServletContext().getInitParameter("bookshelf.callback"))
            .execute();

    req.getSession().setAttribute("token", tokenResponse.toString()); // Keep track of the token.
    final Credential credential = flow.createAndStoreCredential(tokenResponse, null);
    final HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);

    final GenericUrl url = new GenericUrl(USERINFO_ENDPOINT);      // Make an authenticated request.
    final HttpRequest request = requestFactory.buildGetRequest(url);
    request.getHeaders().setContentType("application/json");

    final String jsonIdentity = request.execute().parseAsString();
    @SuppressWarnings("unchecked")
    HashMap<String, String> userIdResult =
        new ObjectMapper().readValue(jsonIdentity, HashMap.class);
    // From this map, extract the relevant profile info and store it in the session.
    req.getSession().setAttribute("userEmail", userIdResult.get("email"));
    req.getSession().setAttribute("userId", userIdResult.get("sub"));
    req.getSession().setAttribute("userImageUrl", userIdResult.get("picture"));
    logger.log(Level.INFO, "Login successful, redirecting to "
        + (String) req.getSession().getAttribute("loginDestination"));
    resp.sendRedirect((String) req.getSession().getAttribute("loginDestination"));
  }
}
// [END example]
