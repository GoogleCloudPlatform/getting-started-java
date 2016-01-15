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

package com.example.appengine.gettingstartedjava.objects;

// [START example]
public class Book {

  private String title;
  private String author;
  private String createdBy;
  private String createdById;
  private String publishedDate;
  private String description;
  private Long id;
  private String imageUrl;
  public static final String AUTHOR = "author";
  public static final String CREATED_BY = "createdBy";
  public static final String CREATED_BY_ID = "createdById";
  public static final String DESCRIPTION = "description";
  public static final String ID = "id";
  public static final String PUBLISHED_DATE = "publishedDate";
  public static final String TITLE = "title";
  public static final String IMAGE_URL = "imageUrl";

  // We use a Builder pattern here to simplify and standardize construction of Book objects.
  private Book(Builder b) {
    this.title = b.title;
    this.author = b.author;
    this.createdBy = b.createdBy;
    this.createdById = b.createdById;
    this.publishedDate = b.publishedDate;
    this.description = b.description;
    this.id = b.id;
    this.imageUrl = b.imageUrl;
  }

  public static class Builder {
    private String title;
    private String author;
    private String createdBy;
    private String createdById;
    private String publishedDate;
    private String description;
    private Long id;
    private String imageUrl;

    public Builder title(String title) {
      this.title = title;
      return this;
    }

    public Builder author(String author) {
      this.author = author;
      return this;
    }

    public Builder createdBy(String createdBy) {
      this.createdBy = createdBy;
      return this;
    }

    public Builder createdById(String createdById) {
      this.createdById = createdById;
      return this;
    }

    public Builder publishedDate(String publishedDate) {
      this.publishedDate = publishedDate;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    public Builder imageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
      return this;
    }

    public Book build() {
      return new Book(this);
    }
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getCreatedById() {
    return createdById;
  }

  public void setCreatedById(String createdById) {
    this.createdById = createdById;
  }

  public String getPublishedDate() {
    return publishedDate;
  }

  public void setPublishedDate(String publishedDate) {
    this.publishedDate = publishedDate;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  @Override
  public String toString() {
    return
        "Title: " + title + ", Author: " + author + ", Published date: " + publishedDate
        + ", Added by: " + createdBy;
  }
}
// [END example]
