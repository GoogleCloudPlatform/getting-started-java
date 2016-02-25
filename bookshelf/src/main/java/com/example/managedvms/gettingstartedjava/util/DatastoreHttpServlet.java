package com.example.managedvms.gettingstartedjava.util;

import com.google.gcloud.datastore.Datastore;
import com.google.gcloud.datastore.DatastoreException;
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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * Since session variables are currently not available across Managed VM instances,
 * we extend HttpServlet to use datastore as a substitute for session variables.
 */
@SuppressWarnings("serial")
public abstract class DatastoreHttpServlet extends HttpServlet {

  private static Datastore datastore;
  private static KeyFactory keyFactory;
  private static final Logger logger = Logger.getLogger(DatastoreHttpServlet.class.getName());
  private static final DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyyMMddHHmmssSSS");

  static {
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


  /**
   * Delete a value stored in the project's datastore.
   * @param sessionId Request from which the session is extracted.
   */
  protected void deleteSessionVariable(String sessionId, String... varNames) {
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

  protected void deleteSession(String sessionId) {
    Key key = keyFactory.newKey(sessionId);
    Transaction transaction = datastore.newTransaction();
    try {
      transaction.delete(key);
      transaction.commit();
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
   * Retrieves a value stored in the project's datastore.
   * @param sessionId Request from which to extract session.
   * @return the value of the requested session variable
   */
  protected String getSessionVariable(String sessionId, String varName)
      throws ServletException {
    Key key = keyFactory.newKey(sessionId);
    try {
      Entity stateEntity = datastore.get(key);
      String state = stateEntity.getString(varName);
      return state;
    } catch (NullPointerException e) {
      logger.log(Level.WARNING, "Did not find a session for id " + sessionId, e);
      return "";
    } catch (DatastoreException e) {
      logger.log(
          Level.WARNING, "Error occurred while finding requested variable name " + varName, e);
      return "";
    }
  }

  /**
   * stores the state value in the project's datastore.
   * @param sessionId Request from which to extract session.
   * @param varName the name of the desired session variable
   * @param varValue the value of the desired session variable
   */
  protected void setSessionVariable(String sessionId, String varName, String varValue)
      throws ServletException {
    Key key = keyFactory.newKey(sessionId);
    Transaction transaction = datastore.newTransaction();
    DateTime dt = DateTime.now(DateTimeZone.UTC);
    dt.toString(dtf);
    try {
      Entity stateEntity = transaction.get(key);
      Entity.Builder seBuilder;
      if (stateEntity == null) {
        logger.log(Level.INFO, "Datastore state entity was empty before get");
        seBuilder = Entity.builder(key).set(varName, varValue);
      } else {
        seBuilder = Entity.builder(stateEntity).set(varName, varValue);
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
   * Returns a set of all session variable names.
   * @param sessionId Request from which to extract session.
   * @return a set of all session variable names
   */
  protected Set<String> listSessionVariables(String sessionId) throws ServletException {
    Key key = keyFactory.newKey(sessionId);
    Entity stateEntity = datastore.get(key);
    // if the datastore state entity doesn't exist, create it before proceeding
    if (stateEntity == null) {
      return new HashSet<>();
    }
    return stateEntity.names();
  }

  /**
   * Take an HttpServletRequest, and copy all of the current session variables over to it
   * @param req Request from which to extract session.
   */
  protected void loadSessionVariables(HttpServletRequest req) throws ServletException {
    String sessionId = req.getSession().getId();
    Key key = keyFactory.newKey(sessionId);
    Entity stateEntity = datastore.get(key);
    // if the datastore state entity doesn't exist, create it before proceeding
    if (stateEntity != null) {
      for (String varName : stateEntity.names()) {
        req.setAttribute(varName, stateEntity.getString(varName));
      }
    }
    logger.log(Level.INFO, "datastore state entity was empty before get");
  }
}
