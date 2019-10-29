package com.example.getstarted.util;

import com.example.getstarted.daos.BookDao;
import com.example.getstarted.daos.FirestoreDao;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener("Creates a connection pool that is stored in the Servlet's context for later use.")
public class BookDaoContextListener implements ServletContextListener {
  @Override
  public void contextDestroyed(javax.servlet.ServletContextEvent event) {
  }

  @Override
  public void contextInitialized(ServletContextEvent event) {
    // This function is called when the application starts and will safely create a connection pool
    // that can be used to connect to.

    BookDao dao = (BookDao) event.getServletContext().getAttribute("dao");
    if (dao == null) {
      dao = new FirestoreDao();
      event.getServletContext().setAttribute("dao", dao);
    }
  }
}
