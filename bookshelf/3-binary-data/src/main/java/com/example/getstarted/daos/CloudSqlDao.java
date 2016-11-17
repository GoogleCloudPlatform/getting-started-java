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

package com.example.getstarted.daos;

import com.example.getstarted.objects.Book;
import com.example.getstarted.objects.Result;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// [START example]
public class CloudSqlDao implements BookDao {
  // [START constructor]
  private static final BasicDataSource dataSource = new BasicDataSource();

  /**
   * A data access object for Bookshelf using a Google Cloud SQL server for storage.
   */
  public CloudSqlDao(final String url) throws SQLException {

    dataSource.setUrl(url);
    final String createTableSql = "CREATE TABLE IF NOT EXISTS books3 ( id INT NOT NULL "
        + "AUTO_INCREMENT, author VARCHAR(255), createdBy VARCHAR(255), createdById VARCHAR(255), "
        + "description VARCHAR(255), publishedDate VARCHAR(255), title VARCHAR(255), imageUrl "
        + "VARCHAR(255), PRIMARY KEY (id))";
    try (Connection conn = dataSource.getConnection()) {
      conn.createStatement().executeUpdate(createTableSql);
    }
  }
  // [END constructor]
  // [START create]
  @Override
  public Long createBook(Book book) throws SQLException {
    final String createBookString = "INSERT INTO books3 "
        + "(author, createdBy, createdById, description, publishedDate, title, imageUrl) "
        + "VALUES (?, ?, ?, ?, ?, ?, ?)";
    try (Connection conn = dataSource.getConnection();
        final PreparedStatement createBookStmt = conn.prepareStatement(createBookString,
            Statement.RETURN_GENERATED_KEYS)) {
      createBookStmt.setString(1, book.getAuthor());
      createBookStmt.setString(2, book.getCreatedBy());
      createBookStmt.setString(3, book.getCreatedById());
      createBookStmt.setString(4, book.getDescription());
      createBookStmt.setString(5, book.getPublishedDate());
      createBookStmt.setString(6, book.getTitle());
      createBookStmt.setString(7, book.getImageUrl());
      createBookStmt.executeUpdate();
      try (ResultSet keys = createBookStmt.getGeneratedKeys()) {
        keys.next();
        return keys.getLong(1);
      }
    }
  }
  // [END create]
  // [START read]
  @Override
  public Book readBook(Long bookId) throws SQLException {
    final String readBookString = "SELECT * FROM books3 WHERE id = ?";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement readBookStmt = conn.prepareStatement(readBookString)) {
      readBookStmt.setLong(1, bookId);
      try (ResultSet keys = readBookStmt.executeQuery()) {
        keys.next();
        return new Book.Builder()
            .author(keys.getString(Book.AUTHOR))
            .createdBy(keys.getString(Book.CREATED_BY))
            .createdById(keys.getString(Book.CREATED_BY_ID))
            .description(keys.getString(Book.DESCRIPTION))
            .id(keys.getLong(Book.ID))
            .publishedDate(keys.getString(Book.PUBLISHED_DATE))
            .title(keys.getString(Book.TITLE))
            .imageUrl(keys.getString(Book.IMAGE_URL))
            .build();
      }
    }
  }
  // [END read]
  // [START update]
  @Override
  public void updateBook(Book book) throws SQLException {
    final String updateBookString = "UPDATE books3 SET author = ?, createdBy = ?, createdById = ?, "
        + "description = ?, publishedDate = ?, title = ?, imageUrl = ? WHERE id = ?";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement updateBookStmt = conn.prepareStatement(updateBookString)) {
      updateBookStmt.setString(1, book.getAuthor());
      updateBookStmt.setString(2, book.getCreatedBy());
      updateBookStmt.setString(3, book.getCreatedById());
      updateBookStmt.setString(4, book.getDescription());
      updateBookStmt.setString(5, book.getPublishedDate());
      updateBookStmt.setString(6, book.getTitle());
      updateBookStmt.setString(7, book.getImageUrl());
      updateBookStmt.setLong(8, book.getId());
      updateBookStmt.executeUpdate();
    }
  }
  // [END update]
  // [START delete]
  @Override
  public void deleteBook(Long bookId) throws SQLException {
    final String deleteBookString = "DELETE FROM books3 WHERE id = ?";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement deleteBookStmt = conn.prepareStatement(deleteBookString)) {
      deleteBookStmt.setLong(1, bookId);
      deleteBookStmt.executeUpdate();
    }
  }
  // [END delete]
  // [START listbooks]
  @Override
  public Result<Book> listBooks(String cursor) throws SQLException {
    int offset = 0;
    if (cursor != null && !cursor.equals("")) {
      offset = Integer.parseInt(cursor);
    }
    final String listBooksString = "SELECT SQL_CALC_FOUND_ROWS author, createdBy, createdById, "
        + "description, id, publishedDate, title, imageUrl FROM books3 ORDER BY title ASC "
        + "LIMIT 10 OFFSET ?";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement listBooksStmt = conn.prepareStatement(listBooksString)) {
      listBooksStmt.setInt(1, offset);
      List<Book> resultBooks = new ArrayList<>();
      try (ResultSet rs = listBooksStmt.executeQuery()) {
        while (rs.next()) {
          Book book = new Book.Builder()
              .author(rs.getString(Book.AUTHOR))
              .createdBy(rs.getString(Book.CREATED_BY))
              .createdById(rs.getString(Book.CREATED_BY_ID))
              .description(rs.getString(Book.DESCRIPTION))
              .id(rs.getLong(Book.ID))
              .publishedDate(rs.getString(Book.PUBLISHED_DATE))
              .title(rs.getString(Book.TITLE))
              .imageUrl(rs.getString(Book.IMAGE_URL))
              .build();
          resultBooks.add(book);
        }
      }
      try (ResultSet rs = conn.createStatement().executeQuery("SELECT FOUND_ROWS()")) {
        int totalNumRows = 0;
        if (rs.next()) {
          totalNumRows = rs.getInt(1);
        }
        if (totalNumRows > offset + 10) {
          return new Result<>(resultBooks, Integer.toString(offset + 10));
        } else {
          return new Result<>(resultBooks);
        }
      }
    }
  }
  // [END listbooks]
}
// [END example]
