/* Copyright 2019 Google LLC
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
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

// [START bookshelf_firestore_client]
public class FirestoreDao implements BookDao {
  private CollectionReference booksCollection;

  public FirestoreDao() {
    Firestore firestore = FirestoreOptions.getDefaultInstance().getService();
    booksCollection = firestore.collection("books");
  }

  // [START bookshelf_firestore_document_to_book]
  private Book documentToBook(DocumentSnapshot document) {
    Map<String, Object> data = document.getData();
    if (data == null) {
      System.out.println("No data in document " + document.getId());
      return null;
    }

    return new Book.Builder()
        .author((String) data.get(Book.AUTHOR))
        .description((String) data.get(Book.DESCRIPTION))
        .publishedDate((String) data.get(Book.PUBLISHED_DATE))
        .imageUrl((String) data.get(Book.IMAGE_URL))
        .createdBy((String) data.get(Book.CREATED_BY))
        .createdById((String) data.get(Book.CREATED_BY_ID))
        .title((String) data.get(Book.TITLE))
        .id(document.getId())
        .build();
  }
  // [END bookshelf_firestore_document_to_book]

  // [START bookshelf_firestore_create_book]
  @Override
  public String createBook(Book book) {
    String id = UUID.randomUUID().toString();
    DocumentReference document = booksCollection.document(id);
    Map<String, Object> data = Maps.newHashMap();

    data.put(Book.AUTHOR, book.getAuthor());
    data.put(Book.DESCRIPTION, book.getDescription());
    data.put(Book.PUBLISHED_DATE, book.getPublishedDate());
    data.put(Book.TITLE, book.getTitle());
    data.put(Book.IMAGE_URL, book.getImageUrl());
    data.put(Book.CREATED_BY, book.getCreatedBy());
    data.put(Book.CREATED_BY_ID, book.getCreatedById());
    try {
      document.set(data).get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }

    return id;
  }
  // [END bookshelf_firestore_create_book]

  // [START bookshelf_firestore_read]
  @Override
  public Book readBook(String bookId) {
    try {
      DocumentSnapshot document = booksCollection.document(bookId).get().get();

      return documentToBook(document);
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
    return null;
  }
  // [END bookshelf_firestore_read]

  // [START bookshelf_firestore_update]
  @Override
  public void updateBook(Book book) {
    DocumentReference document = booksCollection.document(book.getId());
    Map<String, Object> data = Maps.newHashMap();

    data.put(Book.AUTHOR, book.getAuthor());
    data.put(Book.DESCRIPTION, book.getDescription());
    data.put(Book.PUBLISHED_DATE, book.getPublishedDate());
    data.put(Book.TITLE, book.getTitle());
    data.put(Book.IMAGE_URL, book.getImageUrl());
    data.put(Book.CREATED_BY, book.getCreatedBy());
    data.put(Book.CREATED_BY_ID, book.getCreatedById());
    try {
      document.set(data).get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
  }
  // [END bookshelf_firestore_update]

  // [START bookshelf_firestore_delete]
  @Override
  public void deleteBook(String bookId) {
    try {
      booksCollection.document(bookId).delete().get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
  }
  // [END bookshelf_firestore_delete]

  // [START bookshelf_firestore_documents_to_books]
  private List<Book> documentsToBooks(List<QueryDocumentSnapshot> documents) {
    List<Book> resultBooks = new ArrayList<>();
    for (QueryDocumentSnapshot snapshot : documents) {
      resultBooks.add(documentToBook(snapshot));
    }
    return resultBooks;
  }
  // [END bookshelf_firestore_documents_to_books]

  // [START bookshelf_firestore_list_books]
  @Override
  public Result<Book> listBooks(String startTitle) {
    Query booksQuery = booksCollection.orderBy("title").limit(10);
    if (startTitle != null) {
      booksQuery = booksQuery.startAfter(startTitle);
    }
    try {
      QuerySnapshot snapshot = booksQuery.get().get();
      List<Book> results = documentsToBooks(snapshot.getDocuments());
      String newCursor = null;
      if (results.size() > 0) {
        newCursor = results.get(results.size() - 1).getTitle();
      }
      return new Result<>(results, newCursor);
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
    return new Result<>(Lists.newArrayList(), null);
  }
  // [END bookshelf_firestore_list_books]

  // [START bookshelf_firestore_list_by_user]
  @Override
  public Result<Book> listBooksByUser(String userId, String startTitle) {
    Query booksQuery =
        booksCollection.orderBy("title").whereEqualTo(Book.CREATED_BY_ID, userId).limit(10);
    if (startTitle != null) {
      booksQuery = booksQuery.startAfter(startTitle);
    }
    try {
      QuerySnapshot snapshot = booksQuery.get().get();
      List<Book> results = documentsToBooks(snapshot.getDocuments());
      String newCursor = null;
      if (results.size() > 0) {
        newCursor = results.get(results.size() - 1).getTitle();
      }
      return new Result<>(results, newCursor);
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
    return new Result<>(Lists.newArrayList(), null);
  }
  // [END bookshelf_firestore_list_by_user]
}
// [END bookshelf_firestore_client]
