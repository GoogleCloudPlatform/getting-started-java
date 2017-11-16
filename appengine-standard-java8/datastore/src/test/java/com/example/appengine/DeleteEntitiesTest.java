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
import static org.junit.Assert.fail;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.util.Base64;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit tests for {@link DeleteEntities}. */
@RunWith(JUnit4.class)
public final class DeleteEntitiesTest {

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
  public void entityDelete() throws Exception {
    // Test the deletion of an entity

    // Setup

    // Create and store an entity for deletion
    Entity newsPost = new Entity("Blogpost", "breakingnews");
    datastore.put(newsPost);

    // Grab the key, encode it into a web-safe string
    String keyString = KeyFactory.keyToString(newsPost.getKey());
    String encodedId =
        new String(Base64.getUrlEncoder().encodeToString(String.valueOf(keyString).getBytes()));

    // Recreate the key from the web-safe string
    String decodedKey = new String(Base64.getUrlDecoder().decode(encodedId));
    Key deleteEntityKey = KeyFactory.stringToKey(decodedKey);

    // Delete the entity
    datastore.delete(deleteEntityKey);

    // Check
    try {
      // Try and retrieve the entity with the same key
      Entity got = datastore.get(deleteEntityKey);

      // Expect a failure
      fail("Expected EntityNotFoundException");
    } catch (EntityNotFoundException expected) {
      assertThat(expected.getKey().getName()).named("exception key name").isEqualTo("breakingnews");
    }
  }
}
