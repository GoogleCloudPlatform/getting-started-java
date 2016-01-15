/**
 * Copyright 2015 Google Inc. All Rights Reserved.
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

package com.example.appengine.gettingstartedjava.daos;

import com.google.gcloud.datastore.Cursor;
import com.google.gcloud.datastore.Datastore;
import com.google.gcloud.datastore.DatastoreOptions;
import com.google.gcloud.datastore.Entity;
import com.google.gcloud.datastore.FullEntity;
import com.google.gcloud.datastore.IncompleteKey;
import com.google.gcloud.datastore.Key;
import com.google.gcloud.datastore.KeyFactory;
import com.google.gcloud.datastore.Query;
import com.google.gcloud.datastore.QueryResults;
import com.google.gcloud.datastore.StructuredQuery.OrderBy;
import com.google.gcloud.datastore.StructuredQuery.PropertyFilter;

import com.example.appengine.gettingstartedjava.objects.Book;
import com.example.appengine.gettingstartedjava.objects.Result;

import java.util.ArrayList;
import java.util.List;

// [START example]
public class DatastoreDao implements BookDao {

  private Datastore datastore;
  private KeyFactory keyFactory;

  public DatastoreDao() {
    datastore = DatastoreOptions
        .builder()
        .projectId(System.getenv("PROJECT_ID"))
        .build()
        .service();
    keyFactory = datastore.newKeyFactory().kind("Book");
  }

  @Override
  public Long createBook(Book book) {
    IncompleteKey key = keyFactory.kind("Book").newKey();
    FullEntity<IncompleteKey> incBookEntity = Entity.builder(key)
        .set(Book.AUTHOR, book.getAuthor())
        .set(Book.CREATED_BY, book.getCreatedBy())
        .set(Book.CREATED_BY_ID, book.getCreatedById())
        .set(Book.DESCRIPTION, book.getDescription())
        .set(Book.PUBLISHED_DATE, book.getPublishedDate())
        .set(Book.TITLE, book.getTitle())
        .set(Book.IMAGE_URL, book.getImageUrl())
        .build();
    Entity bookEntity = datastore.add(incBookEntity);
    return bookEntity.key().id();
  }

  @Override
  public Book readBook(Long bookId) {
    Entity bookEntity = datastore.get(keyFactory.newKey(bookId));
    return new Book.Builder()
        .author(bookEntity.getString(Book.AUTHOR))
        .createdBy(
            bookEntity.contains(Book.CREATED_BY) ? bookEntity.getString(Book.CREATED_BY) : "")
        .createdById(
            bookEntity.contains(
                Book.CREATED_BY_ID) ? bookEntity.getString(Book.CREATED_BY_ID) : "")
        .description(bookEntity.getString(Book.DESCRIPTION))
        .id(bookEntity.key().id())
        .publishedDate(bookEntity.getString(Book.PUBLISHED_DATE))
        .title(bookEntity.getString(Book.TITLE))
        .imageUrl(bookEntity.contains(Book.IMAGE_URL) ? bookEntity.getString(Book.IMAGE_URL) : null)
        .build();
  }

  @Override
  public void updateBook(Book book) {
    Key key = keyFactory.newKey(book.getId());
    Entity entity = Entity.builder(key)
        .set(Book.AUTHOR, book.getAuthor())
        .set(Book.CREATED_BY, book.getCreatedBy())
        .set(Book.CREATED_BY_ID, book.getCreatedById())
        .set(Book.DESCRIPTION, book.getDescription())
        .set(Book.PUBLISHED_DATE, book.getPublishedDate())
        .set(Book.TITLE, book.getTitle())
        .set(Book.IMAGE_URL, book.getImageUrl())
        .build();
    datastore.update(entity);
  }

  @Override
  public void deleteBook(Long bookId) {
    Key key = keyFactory.newKey(bookId);
    datastore.delete(key);
  }

  @Override
  public Result<Book> listBooks(String startCursorString) {
    Cursor startCursor = null;
    if(startCursorString != null && !startCursorString.equals("")) {
          startCursor = Cursor.fromUrlSafe(startCursorString);
    }
    Query<Entity> q = Query.entityQueryBuilder()
        .kind("Book")
        .limit(10)
        .startCursor(startCursor)
        .orderBy(OrderBy.asc("title"))
        .build();
    QueryResults<Entity> resultList = datastore.run(q);
    List<Book> resultBooks = new ArrayList<>();
    while(resultList.hasNext()) {
      Entity bookEntity = resultList.next();
      Book book = new Book.Builder()
          .author(bookEntity.getString(Book.AUTHOR))
          .createdBy(
              bookEntity.contains(Book.CREATED_BY) ? bookEntity.getString(Book.CREATED_BY) : "")
          .createdById(
              bookEntity.contains(
                  Book.CREATED_BY_ID) ? bookEntity.getString(Book.CREATED_BY_ID) : "")
          .description(bookEntity.getString(Book.DESCRIPTION))
          .id(bookEntity.key().id())
          .publishedDate(bookEntity.getString(Book.PUBLISHED_DATE))
          .title(bookEntity.getString(Book.TITLE))
          .imageUrl(
              bookEntity.contains(Book.IMAGE_URL) ? bookEntity.getString(Book.IMAGE_URL) : null)
          .build();
      resultBooks.add(book);
    }
    Cursor cursor = resultList.cursorAfter(); // note cursorAfter() doesn't work currently
    if(cursor != null)  {
      String cursorString = cursor.toUrlSafe();
      return new Result<>(resultBooks, cursorString);
    } else {
      return new Result<>(resultBooks);
    }
  }

  @Override
  public Result<Book> listBooksByUser(String userId, String startCursorString) throws Exception {
    Cursor startCursor = null;
    if(startCursorString != null && !startCursorString.equals("")) {
          startCursor = Cursor.fromUrlSafe(startCursorString);
    }
    Query<Entity> q = Query.entityQueryBuilder()
        .kind("Book")
        .filter(PropertyFilter.eq(Book.CREATED_BY_ID, userId))
        .limit(10)
        .startCursor(startCursor)
        // a custom datastore index is required since you are filtering by one property
        // but ordering by another
        .orderBy(OrderBy.asc(Book.TITLE))
        .build();
    QueryResults<Entity> resultList = datastore.run(q);
    List<Book> resultBooks = new ArrayList<>();
    while(resultList.hasNext()) {
      Entity bookEntity = resultList.next();
      Book book = new Book.Builder()
          .author(bookEntity.getString(Book.AUTHOR))
          .createdBy(
              bookEntity.contains(Book.CREATED_BY) ? bookEntity.getString(Book.CREATED_BY) : "")
          .createdById(
              bookEntity.contains(
                  Book.CREATED_BY_ID) ? bookEntity.getString(Book.CREATED_BY_ID) : "")
          .description(bookEntity.getString(Book.DESCRIPTION))
          .id(bookEntity.key().id())
          .publishedDate(bookEntity.getString(Book.PUBLISHED_DATE))
          .title(bookEntity.getString(Book.TITLE))
          .imageUrl(
              bookEntity.contains(Book.IMAGE_URL) ? bookEntity.getString(Book.IMAGE_URL) : null)
          .build();
      resultBooks.add(book);
    }
    Cursor cursor = resultList.cursorAfter(); // note cursorAfter() doesn't work currently
    if(cursor != null)  {
      String cursorString = cursor.toUrlSafe();
      return new Result<>(resultBooks, cursorString);
    } else {
      return new Result<>(resultBooks);
    }
  }
}
// [END example]
