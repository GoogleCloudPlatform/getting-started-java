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

import com.example.appengine.gettingstartedjava.objects.Book;
import com.example.appengine.gettingstartedjava.objects.Result;

public interface BookDao {

  public Long createBook(Book book) throws Exception;

  public Book readBook(Long bookId) throws Exception;

  public void updateBook(Book book) throws Exception;

  public void deleteBook(Long bookId) throws Exception;

  public Result<Book> listBooks(String startCursor) throws Exception;

  public Result<Book> listBooksByUser(String userId, String startCursor) throws Exception;
}
