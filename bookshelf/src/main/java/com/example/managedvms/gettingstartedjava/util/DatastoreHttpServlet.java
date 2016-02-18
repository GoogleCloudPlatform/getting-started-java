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
import javax.servlet.http.HttpServlet;

/**
 * Since session variables are currently not available across Managed VM instances,
 * we extend HttpServlet to use datastore as a substitute for session variables.
 */
@SuppressWarnings("serial")
public abstract class DatastoreHttpServlet extends HttpServlet {

  private Datastore datastore;
  private Logger logger = Logger.getLogger(this.getClass().getName());
  private KeyFactory keyFactory;
  private Key key;
  public void init() throws ServletException {
    datastore = DatastoreOptions.defaultInstance().service();
    keyFactory = datastore.newKeyFactory().kind("SessionVariables");
    key = keyFactory.newKey("bookshelf");
  }

  /**
   * Delete a value stored in the project's datastore.
   * @param varName
   */
  protected void deleteSessionVariable(String varName) {
    Entity stateEntity = datastore.get(key);
    Entity newEntity = Entity.builder(stateEntity).remove(varName).build();
    datastore.update(newEntity);
  }

  /**
   * Retrieve a value stored in the project's datastore.
   * do i still need? just list session variables at the beginning, otherwise, have multiple gets
   * @param varName
   * @return
   */
  protected String getSessionVariable(String varName) {
    Entity stateEntity = datastore.get(key);
    String state = stateEntity.getString(varName);
    return state;
  }

  /**
   * stores the state value in the project's datastore.
   * @param varName TODO
   * @param varValue
   */
  protected void setSessionVariable(String varName, String varValue) {
    Entity stateEntity = datastore.get(key);
    // add the new value
    logger.log(Level.INFO, "setting new session variable " + varName + " to " + varValue);
    datastore.put(Entity.builder(stateEntity).set(varName, varValue).build());
  }

  protected Set<String> listSessionVariables() {
    Entity stateEntity = datastore.get(key);
    if (stateEntity == null) {
      logger.log(Level.INFO, "state entity was empty before get");
      stateEntity = Entity.builder(key).build();
      datastore.put(stateEntity);
    }
    return stateEntity.names();
  }
}
