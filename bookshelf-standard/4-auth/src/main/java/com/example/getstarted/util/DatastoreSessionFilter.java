/* Copyright 2016 Google Inc.
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

package com.example.getstarted.util;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Transaction;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

// [START init]
public class DatastoreSessionFilter implements Filter {

  private static DatastoreService datastore;
  private static final DateTimeFormatter DTF = DateTimeFormat.forPattern("yyyyMMddHHmmssSSS");
  private static final String SESSION_KIND = "SessionVariable";

  @Override
  public void init(FilterConfig config) throws ServletException {
    // initialize local copy of datastore session variables

    datastore = DatastoreServiceFactory.getDatastoreService();
    // Delete all sessions unmodified for over two days
    DateTime dt = DateTime.now(DateTimeZone.UTC);
    Query query = new Query(SESSION_KIND).setFilter(new FilterPredicate(
            "lastModified", FilterOperator.LESS_THAN_OR_EQUAL, dt.minusDays(2).toString(DTF)));
    Iterator<Entity> results = datastore.prepare(query).asIterator();
    while (results.hasNext()) {
      Entity stateEntity = results.next();
      datastore.delete(stateEntity.getKey());
    }
  }
  // [END init]

  @Override
  public void doFilter(ServletRequest servletReq, ServletResponse servletResp, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) servletReq;
    HttpServletResponse resp = (HttpServletResponse) servletResp;

    // Check if the session cookie is there, if not there, make a session cookie using a unique
    // identifier.
    String sessionId = getCookieValue(req, "bookshelfSessionId");
    if (sessionId.equals("")) {
      String sessionNum = new BigInteger(130, new SecureRandom()).toString(32);
      Cookie session = new Cookie("bookshelfSessionId", sessionNum);
      session.setPath("/");
      resp.addCookie(session);
    }

    Map<String,String> datastoreMap = loadSessionVariables(req);  // session variables for request

    chain.doFilter(servletReq, servletResp);  // Allow the servlet to process request and response

    HttpSession session = req.getSession();   // Create session map
    Map<String, String> sessionMap = new HashMap<>();
    Enumeration<String> attrNames = session.getAttributeNames();
    while (attrNames.hasMoreElements()) {
      String attrName = attrNames.nextElement();
      sessionMap.put(attrName, (String) session.getAttribute(attrName));
    }

    // Create a diff between the new session variables and the existing session variables
    // to minimize datastore access
    MapDifference<String, String> diff = Maps.difference(sessionMap, datastoreMap);
    Map<String, String> setMap = diff.entriesOnlyOnLeft();
    Map<String, String> deleteMap = diff.entriesOnlyOnRight();

    // Apply the diff
    setSessionVariables(sessionId, setMap);
    deleteSessionVariables(
        sessionId,
        FluentIterable.from(deleteMap.keySet()).toArray(String.class));
  }

  @SuppressWarnings("unused")
  private String mapToString(Map<String, String> map) {
    StringBuffer names = new StringBuffer();
    for (String name : map.keySet()) {
      names.append(name + " ");
    }
    return names.toString();
  }

  @Override
  public void destroy() {
  }

  protected String getCookieValue(HttpServletRequest req, String cookieName) {
    Cookie[] cookies = req.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(cookieName)) {
          return cookie.getValue();
        }
      }
    }
    return "";
  }

  // [START deleteSessionVariables]
  /**
   * Delete a value stored in the project's datastore.
   * @param sessionId Request from which the session is extracted.
   */
  protected void deleteSessionVariables(String sessionId, String... varNames) {
    if (sessionId.equals("")) {
      return;
    }
    Key key = KeyFactory.createKey(SESSION_KIND, sessionId);
    Transaction transaction = datastore.beginTransaction();
    try {
      Entity stateEntity = datastore.get(transaction, key);
      for (String varName : varNames) {
        stateEntity.removeProperty(varName);
      }
      datastore.put(transaction, stateEntity);
      transaction.commit();
    } catch (EntityNotFoundException e) {
      // Ignore - if there's no session, there's nothing to delete.
    } finally {
      if (transaction.isActive()) {
        transaction.rollback();
      }
    }
  }
  // [END deleteSessionVariables]

  protected void deleteSessionWithValue(String varName, String varValue) {
    Transaction transaction = datastore.beginTransaction();
    try {
      Query query = new Query(SESSION_KIND)
          .setFilter(new FilterPredicate(varName, FilterOperator.EQUAL, varValue));
      Iterator<Entity> results = datastore.prepare(transaction, query).asIterator();
      while (results.hasNext()) {
        Entity stateEntity = results.next();
        datastore.delete(transaction, stateEntity.getKey());
      }
      transaction.commit();
    } finally {
      if (transaction.isActive()) {
        transaction.rollback();
      }
    }
  }

  // [START setSessionVariables]
  /**
   * Stores the state value in each key-value pair in the project's datastore.
   * @param sessionId Request from which to extract session.
   * @param varName the name of the desired session variable
   * @param varValue the value of the desired session variable
   */
  protected void setSessionVariables(String sessionId, Map<String, String> setMap) {
    if (sessionId.equals("")) {
      return;
    }
    Key key = KeyFactory.createKey(SESSION_KIND, sessionId);
    Transaction transaction = datastore.beginTransaction();
    DateTime dt = DateTime.now(DateTimeZone.UTC);
    dt.toString(DTF);
    try {
      Entity stateEntity;
      try {
        stateEntity = datastore.get(transaction, key);
      } catch (EntityNotFoundException e) {
        stateEntity = new Entity(key);
      }
      for (String varName : setMap.keySet()) {
        stateEntity.setProperty(varName, setMap.get(varName));
      }
      stateEntity.setProperty("lastModified", dt.toString(DTF));
      datastore.put(transaction, stateEntity);
      transaction.commit();
    } finally {
      if (transaction.isActive()) {
        transaction.rollback();
      }
    }
  }
  // [END setSessionVariables]

  // [START loadSessionVariables]
  /**
   * Take an HttpServletRequest, and copy all of the current session variables over to it
   * @param req Request from which to extract session.
   * @return a map of strings containing all the session variables loaded or an empty map.
   */
  protected Map<String, String> loadSessionVariables(HttpServletRequest req)
      throws ServletException {
    Map<String, String> datastoreMap = new HashMap<>();
    String sessionId = getCookieValue(req, "bookshelfSessionId");
    if (sessionId.equals("")) {
      return datastoreMap;
    }
    Key key = KeyFactory.createKey(SESSION_KIND, sessionId);
    Transaction transaction = datastore.beginTransaction();
    try {
      Entity stateEntity = datastore.get(transaction, key);
      Map<String, Object> properties = stateEntity.getProperties();
      for (Map.Entry<String, Object> property : properties.entrySet()) {
        req.getSession().setAttribute(property.getKey(), property.getValue());
        datastoreMap.put(property.getKey(), (String)property.getValue());
      }
      transaction.commit();
    } catch (EntityNotFoundException e) {
      // Ignore - if there's no session, there's nothing to delete.
    } finally {
      if (transaction.isActive()) {
        transaction.rollback();
      }
    }
    return datastoreMap;
  }
  // [END loadSessionVariables]
}
