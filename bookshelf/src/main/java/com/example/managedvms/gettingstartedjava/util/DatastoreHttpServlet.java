package com.example.managedvms.gettingstartedjava.util;

import com.google.gcloud.datastore.Datastore;
import com.google.gcloud.datastore.DatastoreOptions;
import com.google.gcloud.datastore.Entity;
import com.google.gcloud.datastore.Key;
import com.google.gcloud.datastore.KeyFactory;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * Since session variables are currently not available across Managed VM instances,
 * we extend HttpServlet to use datastore as a substitute for session variables.
 */
@SuppressWarnings("serial")
public abstract class DatastoreHttpServlet extends HttpServlet {

  private Datastore datastore;
  private Logger logger = Logger.getLogger(this.getClass().getName());
  private KeyFactory keyFactory;
  public void init() throws ServletException {
    datastore = DatastoreOptions.defaultInstance().service();
    keyFactory = datastore.newKeyFactory().kind("SessionVariables");
  }


  /**
   * Delete a value stored in the project's datastore.
   * @param req Request from which to extract session.
   * @param varName
   * @throws ServletException
   */
  protected void deleteSessionVariable(HttpServletRequest req, String... varNames)
      throws ServletException {
    String sessionId = getSessionId(req);
    Key key = keyFactory.newKey(sessionId);
    Entity stateEntity = datastore.get(key);
    for (String varName : varNames) {
      stateEntity = Entity.builder(stateEntity).remove(varName).build();
    }
    datastore.update(stateEntity);
  }

  /**
   * Retrieve a value stored in the project's datastore.
   * @param req Request from which to extract session.
   * @param varName
   * @return the value of the requested session variable
   * @throws ServletException
   */
  protected String getSessionVariable(HttpServletRequest req, String varName)
      throws ServletException {
    String sessionId = getSessionId(req);
    Key key = keyFactory.newKey(sessionId);
    Entity stateEntity = datastore.get(key);
    String state = stateEntity.getString(varName);
    return state;
  }

  /**
   * stores the state value in the project's datastore.
   * @param req Request from which to extract session.
   * @param varName the name of the desired session variable
   * @param varValue the value of the desired session variable
   * @throws ServletException
   */
  protected void setSessionVariable(HttpServletRequest req, String varName, String varValue)
      throws ServletException {
    String sessionId = getSessionId(req);
    Key key = keyFactory.newKey(sessionId);
    Entity stateEntity = datastore.get(key);
    if (stateEntity == null) {
      logger.log(Level.INFO, "datastore state entity was empty before get");
      stateEntity = Entity.builder(key).build();
      datastore.put(stateEntity);
    }
    datastore.put(
        Entity.builder(stateEntity)
        .set(varName, varValue).build());
  }

  /**
   * Returns a set of all session variable names.
   * @param req Request from which to extract session.
   * @return a set of all session variable names
   * @throws ServletException
   */
  protected Set<String> listSessionVariables(HttpServletRequest req) throws ServletException {
    String sessionId = getSessionId(req);
    Key key = keyFactory.newKey(sessionId);
    Entity stateEntity = datastore.get(key);
    // if the datastore state entity doesn't exist, create it before proceeding
    if (stateEntity == null) {
      logger.log(Level.INFO, "datastore state entity was empty before get");
      stateEntity = Entity.builder(key).build();
      datastore.put(stateEntity);
    }
    return stateEntity.names();
  }

  /**
   * Extracts the Jetty session id from a request
   * @param req Request from which to extract session.
   * @return The session id string
   * @throws ServletException
   */
  protected String getSessionId(HttpServletRequest req) throws ServletException {
    Cookie[] cookies = req.getCookies();
    for (Cookie c : cookies) {
      if (c.getName().equals("JSESSIONID")) {
        return c.getValue();
      }
    }
    throw new ServletException("Could not find a session cookie");
  }

  /**
   * Take an HttpServletRequest, and copy all of the current session variables over to it
   * @param req Request from which to extract session.
   * @throws ServletException
   */
  protected void loadSessionVariables(HttpServletRequest req) throws ServletException {
    String sessionId = getSessionId(req);
    Key key = keyFactory.newKey(sessionId);
    Entity stateEntity = datastore.get(key);
    // if the datastore state entity doesn't exist, create it before proceeding
    if (stateEntity == null) {
      logger.log(Level.INFO, "datastore state entity was empty before get");
      stateEntity = Entity.builder(key).build();
      datastore.put(stateEntity);
    }
    for (String varName : stateEntity.names()) {
      req.setAttribute(varName, stateEntity.getString(varName));
    }
  }
}
