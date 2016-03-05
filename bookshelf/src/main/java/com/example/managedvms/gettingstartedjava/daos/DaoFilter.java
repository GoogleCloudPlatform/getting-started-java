package com.example.managedvms.gettingstartedjava.daos;

import com.example.managedvms.gettingstartedjava.util.CloudStorageHelper;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebFilter(filterName = "DaoFilter", value = "/*")
public class DaoFilter implements Filter {

  private static final Logger logger = Logger.getLogger(DaoFilter.class.getName());

  @Override
  public void init(FilterConfig config) throws ServletException {
    logger.log(Level.INFO, "DaoFilter is initializing");
    BookDao dao = null;
    CloudStorageHelper storageHelper = new CloudStorageHelper();

    // Creates the DAO based on the Context Parameters
    String storageType = config.getServletContext().getInitParameter("bookshelf.storageType");
    switch (storageType) {
      case "datastore":
        dao = new DatastoreDao();
        break;
      case "cloudsql":
        try {
          dao = new CloudSqlDao(config.getServletContext().getInitParameter("sql.url"));
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
