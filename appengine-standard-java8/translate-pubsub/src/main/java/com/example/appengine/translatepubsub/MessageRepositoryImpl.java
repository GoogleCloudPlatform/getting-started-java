/*
 * Copyright 2018 Google Inc.
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

package com.example.appengine.translatepubsub;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Storage for Message objects using Cloud Datastore.
 */
public class MessageRepositoryImpl implements MessageRepository {

  private static MessageRepositoryImpl instance;

  private String messagesKind = "messages";
  private KeyFactory keyFactory = getDatastoreInstance().newKeyFactory().setKind(messagesKind);

  private MessageRepositoryImpl() {
  }

  // retrieve a singleton instance
  public static synchronized MessageRepositoryImpl getInstance() {
    if (instance == null) {
      instance = new MessageRepositoryImpl();
    }
    return instance;
  }

  @Override
  public void save(Message message) {
    // Save message to "messages"
    Datastore datastore = getDatastoreInstance();
    Key key = datastore.allocateId(keyFactory.newKey());

    Entity.Builder messageEntityBuilder = Entity.newBuilder(key)
        .set("messageId", message.getMessageId());

    String translated = message.getTranslated();
    if (translated != null) {
      messageEntityBuilder = messageEntityBuilder.set("data", translated);
    }

    if (message.getPublishTime() != null) {
      messageEntityBuilder = messageEntityBuilder.set("publishTime", message.getPublishTime());
    }

    if (message.getSourceLang() != null) {
      messageEntityBuilder = messageEntityBuilder.set("sourceLang", message.getSourceLang());
    }

    if (message.getTargetLang() != null) {
      messageEntityBuilder = messageEntityBuilder.set("targetLang", message.getTargetLang());
    }
    datastore.put(messageEntityBuilder.build());
  }

  @Override
  public List<Message> retrieve(int limit) {
    // Get Message saved in Datastore
    Datastore datastore = getDatastoreInstance();
    Query<Entity> query =
        Query.newEntityQueryBuilder()
            .setKind(messagesKind)
            .setLimit(limit)
            .addOrderBy(StructuredQuery.OrderBy.desc("publishTime"))
            .build();
    QueryResults<Entity> results = datastore.run(query);

    List<Message> messages = new ArrayList<>();
    while (results.hasNext()) {
      Entity entity = results.next();
      Message message = new Message(entity.getString("messageId"));
      String data = entity.getString("data");
      if (data != null) {
        message.setData(data);
      }
      String publishTime = entity.getString("publishTime");
      if (publishTime != null) {
        message.setPublishTime(publishTime);
      }
      if (entity.contains("sourceLang")) {
        String sourceLang = entity.getString("sourceLang");
        if (sourceLang != null) {
          message.setSourceLang(sourceLang);
        }
      }
      if (entity.contains("targetLang")) {
        String targetLang = entity.getString("targetLang");
        if (targetLang != null) {
          message.setTargetLang(targetLang);
        }
      }
      messages.add(message);
    }
    return messages;
  }

  private Datastore getDatastoreInstance() {
    DatastoreOptions instance = DatastoreOptions.getDefaultInstance();
    return instance.getService();
  }
}
