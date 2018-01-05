/**
 * Copyright 2018 Google Inc.
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

package com.example.appengine.translate_pubsub;

import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "Publish with PubSub", value = "/pubsub/publish")
public class PubSubPublish extends PubSubHttpServlet {

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {
    Publisher publisher = this.publisher;
    String topicId = System.getenv("PUBSUB_TOPIC");
    // create a publisher on the topic
    if (publisher == null) {
      this.publisher = publisher = Publisher.newBuilder(
          TopicName.of(ServiceOptions.getDefaultProjectId(), topicId))
          .build();
    }
    // construct a pubsub message from the message payload.
    Message message = getMessage(req);
    PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
            .setData(ByteString.copyFromUtf8(message.getTranslated()))
            .putAttributes("sourceLang", message.getSourceLang())
            .putAttributes("targetLang", message.getTargetLang())
            .build();

    publisher.publish(pubsubMessage);
    // redirect to home page
    resp.sendRedirect("/");
  }

  private Publisher publisher;

  public PubSubPublish() { }

  PubSubPublish(Publisher publisher) {
    this.publisher = publisher;
  }
}
