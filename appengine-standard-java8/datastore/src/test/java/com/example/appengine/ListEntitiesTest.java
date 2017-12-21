/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.appengine.datatstore;

import static com.google.common.truth.Truth.assertThat;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.ImmutableList;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit tests for {@link ListEntities}. */
@RunWith(JUnit4.class)
public final class ListEntitiesTest {

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          // Set no eventual consistency, that way queries return all results.
          // https://cloud.google.com/appengine/docs/java/tools/localunittesting#Java_Writing_High_Replication_Datastore_tests
          new LocalDatastoreServiceTestConfig()
              .setDefaultHighRepJobPolicyUnappliedJobPercentage(0));

  private DatastoreService datastore;

  @Before
  public void setUp() {
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void listEntities() throws Exception {

    // Datastore query
    final Query q =
        new Query("Blogpost").setFilter(new FilterPredicate("title", FilterOperator.NOT_EQUAL, ""));

    int counter = 1; // Counter for automated test

    // Setup

    // Create six entities, one without a title
    Entity post1 = createEntity("post1", "Title1", "Author1", "Content1");
    Entity post2 = createEntity("post2", "Title2", "Author2", "Content2");
    Entity post3 = createEntity("post3", "Title3", "Author3", "Content3");
    Entity post4 = createEntity("post4", "Title4", "Author4", "Content4");
    Entity post5 = createEntity("post5", "Title5", "Author5", "Content5");
    Entity post6 = createEntity("post6", "", "Author6", "Content6");

    // Store all the entities in Datastore
    datastore.put(ImmutableList.<Entity>of(post1, post2, post3, post4, post5, post6));

    PreparedQuery pq = datastore.prepare(q);
    List<Entity> posts = pq.asList(FetchOptions.Builder.withDefaults());

    assertThat(posts).named("query results").containsExactly(post1, post2, post3, post4, post5);

    for (Entity result : posts) {

      assertThat((String) result.getProperty("title"))
          .named("result.title")
          .isEqualTo("Title" + counter);
      assertThat((String) result.getProperty("author"))
          .named("result.author")
          .isEqualTo("Author" + counter);
      assertThat((String) result.getProperty("body"))
          .named("result.body")
          .isEqualTo("Content" + counter);
      assertThat((Date) result.getProperty("timestamp")).named("result.timestamp").isNotNull();

      counter++;
    }
  }

  private Entity createEntity(String key, String title, String author, String body) {

    Entity newsPost = new Entity("Blogpost", key);
    newsPost.setProperty("title", title);
    newsPost.setProperty("author", author);
    newsPost.setProperty("body", body);
    newsPost.setProperty("timestamp", new Date());

    return newsPost;
  }
}
