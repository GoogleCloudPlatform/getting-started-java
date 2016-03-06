package com.example.managedvms.gettingstartedjava.util;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.gcloud.datastore.Datastore;
import com.google.gcloud.datastore.DatastoreOptions;
import com.google.gcloud.datastore.Entity;
import com.google.gcloud.datastore.Key;
import com.google.gcloud.datastore.KeyFactory;
import com.google.gcloud.datastore.Query;
import com.google.gcloud.datastore.QueryResults;
import com.google.gcloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gcloud.datastore.Transaction;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(filterName = "DatastoreSessionFilter", value = "/*")
public class DatastoreSessionFilter implements Filter {

  private static Datastore datastore;
  private static KeyFactory keyFactory;
  private static Map<String, String> datastoreMap;
  private static final DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyyMMddHHmmssSSS");
  private static final Logger logger = Logger.getLogger(DatastoreSessionFilter.class.getName());

  @Override
  public void init(FilterConfig config) throws ServletException {
    // initialize local copy of datastore session variables
    datastoreMap = new HashMap<>();

    datastore = DatastoreOptions.defaultInstance().service();
    keyFactory = datastore.newKeyFactory().kind("SessionVariable");
    // Delete all sessions unmodified for over two days
    DateTime dt = DateTime.now(DateTimeZone.UTC);
    Query<Entity> query = Query.entityQueryBuilder()
        .kind("SessionVariable")
        .filter(PropertyFilter.le("lastModified", dt.minusDays(2).toString(dtf)))
        .build();
    QueryResults<Entity> resultList = datastore.run(query);
    while (resultList.hasNext()) {
      Entity stateEntity = resultList.next();
      datastore.delete(stateEntity.key());
    }
  }

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

    // load session variables into the request
    loadSessionVariables(req);

    // Allow the servlet to process the request and response
    chain.doFilter(servletReq, servletResp);

    // Create session map
    HttpSession session = req.getSession();
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

    // Update the local copy of the datastore session variable map
    datastoreMap = sessionMap;
  }

  @Override
  public void destroy() {
    logger.log(Level.INFO, "SessionFilter is being destroyed");
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
    logger.log(Level.WARNING, "Cookie with name " + cookieName + " was not found.");
    return "";
  }

  /**
   * Delete a value stored in the project's datastore.
   * @param sessionId Request from which the session is extracted.
   */
  protected void deleteSessionVariables(String sessionId, String... varNames) {
    if (sessionId.equals("")) {
      logger.log(Level.INFO, "Session Id was empty");
      return;
    }
    Key key = keyFactory.newKey(sessionId);
    Transaction transaction = datastore.newTransaction();
    try {
      Entity stateEntity = transaction.get(key);
      Entity.Builder builder = Entity.builder(stateEntity);
      for (String varName : varNames) {
        logger.log(Level.INFO, "removing session var name: " + varName);
        builder = builder.remove(varName);
      }
      datastore.update(builder.build());
    } catch (NullPointerException e) {
      logger.log(Level.WARNING, "Did not find a session for id " + sessionId, e);
    } finally {
      if (transaction.active()) {
        transaction.rollback();
      }
    }
  }

  protected void deleteSessionWithValue(String varName, String varValue) {
    Transaction transaction = datastore.newTransaction();
    try {
      Query<Entity> query = Query.entityQueryBuilder()
          .kind("SessionVariable")
          .filter(PropertyFilter.eq(varName, varValue))
          .build();
      QueryResults<Entity> resultList = transaction.run(query);
      while (resultList.hasNext()) {
        Entity stateEntity = resultList.next();
        transaction.delete(stateEntity.key());
      }
      transaction.commit();
    } finally {
      if (transaction.active()) {
        transaction.rollback();
      }
    }
  }

  /**
   * Stores the state value in each key-value pair in the project's datastore.
   * @param sessionId Request from which to extract session.
   * @param varName the name of the desired session variable
   * @param varValue the value of the desired session variable
   */
  protected void setSessionVariables(String sessionId, Map<String, String> setMap) {
    if (sessionId.equals("")) {
      logger.log(Level.INFO, "Session Id was empty");
      return;
    }
    Key key = keyFactory.newKey(sessionId);
    Transaction transaction = datastore.newTransaction();
    DateTime dt = DateTime.now(DateTimeZone.UTC);
    dt.toString(dtf);
    try {
      Entity stateEntity = transaction.get(key);
      Entity.Builder seBuilder;
      if (stateEntity == null) {
        logger.log(Level.INFO, "Datastore state entity was empty before set");
        seBuilder = Entity.builder(key);
      } else {
        seBuilder = Entity.builder(stateEntity);
      }
      for (String varName : setMap.keySet()) {
        seBuilder.set(varName, setMap.get(varName));
      }
      transaction.put(seBuilder.set("lastModified", dt.toString(dtf)).build());
      transaction.commit();
    } finally {
      if (transaction.active()) {
        transaction.rollback();
      }
    }
  }

  /**
   * Take an HttpServletRequest, and copy all of the current session variables over to it
   * @param req Request from which to extract session.
   */
  protected void loadSessionVariables(HttpServletRequest req) throws ServletException {
    String sessionId = getCookieValue(req, "bookshelfSessionId");
    if (sessionId.equals("")) {
      logger.log(Level.INFO, "Session Id was empty");
      return;
    }
    if (datastoreMap.keySet().size() == 0) {
      logger.log(Level.INFO, "size of datastoreMap: " + datastoreMap.keySet().size());
      return;
    }
    Key key = keyFactory.newKey(sessionId);
    Entity stateEntity = datastore.get(key);
    if (stateEntity != null) {
      for (String varName : stateEntity.names()) {
        req.getSession().setAttribute(varName, stateEntity.getString(varName));
      }
    } else {
      logger.log(Level.INFO, "datastore state entity was empty before get");
      return;
    }
  }
}
