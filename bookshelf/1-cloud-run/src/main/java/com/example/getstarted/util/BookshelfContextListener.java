package com.example.getstarted.util;

import com.example.getstarted.daos.BookDao;
import com.example.getstarted.daos.FirestoreDao;
import com.google.common.base.Strings;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.xpath.operations.Bool;

@WebListener("Creates a connection pool that is stored in the Servlet's context for later use.")
public class BookshelfContextListener implements ServletContextListener {
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

    Bool isCloudStorageConfigured = (Bool) event.getServletContext()
        .getAttribute("isCloudStorageConfigured");
    if (isCloudStorageConfigured == null) {
      event.getServletContext()
          .setAttribute(
              "isCloudStorageConfigured",
              !Strings.isNullOrEmpty(System.getenv("BOOKSHELF_BUCKET")));
    }

    CloudStorageHelper storageHelper = (CloudStorageHelper) event.getServletContext().getAttribute(
        "storageHelper");
    if (storageHelper == null) {
      storageHelper = new CloudStorageHelper();
      event.getServletContext().setAttribute("storageHelper", storageHelper);
    }
  }
}
