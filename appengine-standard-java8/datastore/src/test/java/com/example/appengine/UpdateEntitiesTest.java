/**
 * Copyright 2017 Google Inc.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.appengine.datastore;

import static com.google.common.truth.Truth.assertThat;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.util.Base64;
import java.util.Date;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit tests for {@link UpdateEntities}. */
@RunWith(JUnit4.class)
public final class UpdateEntitiesTest {

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
  public void entityUpdate() throws Exception {

    // Setup
    Entity newsPost = new Entity("Blogpost", "breakingnews");
    newsPost.setProperty("title", "BREAKING NEWS!");
    newsPost.setProperty("author", "News hound");
    newsPost.setProperty("body", "Stop press! Sky is blue.");
    newsPost.setProperty("timestamp", new Date().getTime());

    datastore.put(newsPost);

    // Encode the key of newsPost, then decode it and create an entity with it
    String newsPostKey = KeyFactory.keyToString(newsPost.getKey());
    String encodedKey =
        new String(Base64.getUrlEncoder().encodeToString(String.valueOf(newsPostKey).getBytes()));

    String decodedKey = new String(Base64.getUrlDecoder().decode(encodedKey));

    // Get the entry and update it leaving the author the same
    Entity updateNewsPost = datastore.get(KeyFactory.stringToKey(decodedKey));
    updateNewsPost.setProperty("title", "OLD NEWS!");
    updateNewsPost.setProperty("body", "The sky is not blue.");
    updateNewsPost.setProperty("timestamp", new Date());

    datastore.put(updateNewsPost); // Update the post

    // Check
    Entity got = datastore.get(updateNewsPost.getKey());
    assertThat((String) got.getProperty("title")).named("got.title").isEqualTo("OLD NEWS!");
    assertThat((String) got.getProperty("author")).named("got.author").isEqualTo("News hound");
    assertThat((String) got.getProperty("body"))
        .named("got.body")
        .isEqualTo("The sky is not blue.");
    assertThat((Date) got.getProperty("timestamp")).named("got.timestamp").isNotNull();
  }
}
