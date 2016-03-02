package com.example.managedvms.gettingstartedjava.daos;

import com.example.managedvms.gettingstartedjava.util.CloudStorageHelper;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

@WebFilter(filterName = "DaoFilter", value = "/*")
public class DaoFilter implements Filter {

  private static final Logger logger = Logger.getLogger(DaoFilter.class.getName());

  @Override
  public void init(FilterConfig config) throws ServletException {
    logger.log(Level.INFO, "DaoFilter is initializing");
    BookDao dao = null;
    CloudStorageHelper storageHelper = new CloudStorageHelper();
    // Creates the DAO based on the system property 
    String storageType = System.getProperty("bookshelf.storageType");
    switch (storageType) {
      case "datastore":
        dao = new DatastoreDao();
        break;
      case "cloudsql":
        try {
          dao = new CloudSqlDao();
        } catch (SQLException e) {
          throw new ServletException("SQL error", e);
        }
        break;
      default:
        throw new IllegalStateException(
            "Invalid storage type. Check if bookshelf.storageType property is set.");
    }
    config.getServletContext().setAttribute("dao", dao);
    config.getServletContext().setAttribute("storageHelper", storageHelper);
  }

  @Override
  public void doFilter(ServletRequest servletReq, ServletResponse servletResp, FilterChain chain)
      throws IOException, ServletException {
    chain.doFilter(servletReq, servletResp);
  }

  @Override
  public void destroy() {
    logger.log(Level.INFO, "DaoFilter is de-initializing");
  }
}
