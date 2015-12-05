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

import com.example.appengine.gettingstartedjava.objects.Book;
import com.example.appengine.gettingstartedjava.objects.Result;

import java.util.ArrayList;
import java.util.List;

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
        .set("author", book.getAuthor())
        .set("description", book.getDescription())
        .set("publishedDate", book.getPublishedDate())
        .set("title", book.getTitle())
        .set("imageUrl", book.getImageUrl())
        .build();
    Entity bookEntity = datastore.add(incBookEntity);
    return bookEntity.key().id();
  }

  @Override
  public Book readBook(Long bookId) {
    Entity bookEntity = datastore.get(keyFactory.newKey(bookId));
    return new Book.Builder()
        .author(bookEntity.getString("author"))
        .description(bookEntity.getString("description"))
        .id(bookEntity.key().id())
        .publishedDate(bookEntity.getString("publishedDate"))
        .title(bookEntity.getString("title"))
          // maintain backwards compatibility with
          // books created in 2-structured data which do not have imageUrl property
        .imageUrl(bookEntity.contains("imageUrl") ? bookEntity.getString("imageUrl") : null)
        .build();
  }

  @Override
  public void updateBook(Book book) {
    Key key = keyFactory.newKey(book.getId());
    Entity entity = Entity.builder(key)
        .set("author", book.getAuthor())
        .set("description", book.getDescription())
        .set("publishedDate", book.getPublishedDate())
        .set("title", book.getTitle())
        .set("imageUrl", book.getImageUrl())
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
    Query<Entity> q;
    Cursor startCursor = null;
    if(startCursorString != null && !startCursorString.equals("")) {
          startCursor = Cursor.fromUrlSafe(startCursorString);
    }
    q = Query.entityQueryBuilder()
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
          .author(bookEntity.getString("author"))
          .description(bookEntity.getString("description"))
          .id(bookEntity.key().id())
          .publishedDate(bookEntity.getString("publishedDate"))
          .title(bookEntity.getString("title"))
          // maintain backwards compatibility with
          // books created in 2-structured data which do not have imageUrl property
          .imageUrl(bookEntity.contains("imageUrl") ? bookEntity.getString("imageUrl") : null)
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
