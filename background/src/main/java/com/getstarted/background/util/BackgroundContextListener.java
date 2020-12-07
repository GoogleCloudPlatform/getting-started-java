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

package com.getstarted.background.util;

import com.google.cloud.ServiceOptions;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.pubsub.v1.TopicName;
import java.io.IOException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

// [START background_context_listener]
@WebListener("Creates Firestore and TranslateServlet service clients for reuse between requests.")
public class BackgroundContextListener implements ServletContextListener {
  @Override
  public void contextDestroyed(javax.servlet.ServletContextEvent event) {}

  @Override
  public void contextInitialized(ServletContextEvent event) {
    String firestoreProjectId = System.getenv("FIRESTORE_CLOUD_PROJECT");
    Firestore firestore = (Firestore) event.getServletContext().getAttribute("firestore");
    if (firestore == null) {
      firestore =
          FirestoreOptions.getDefaultInstance().toBuilder()
              .setProjectId(firestoreProjectId)
              .build()
              .getService();
      event.getServletContext().setAttribute("firestore", firestore);
    }

    Translate translate = (Translate) event.getServletContext().getAttribute("translate");
    if (translate == null) {
      translate = TranslateOptions.getDefaultInstance().getService();
      event.getServletContext().setAttribute("translate", translate);
    }

    TopicName topicName = TopicName.of(firestoreProjectId, topicId);
    Publisher publisher = (Publisher) event.getServletContext().getAttribute("publisher");
    if (publisher == null) {
      try {
        String topicId = System.getenv("PUBSUB_TOPIC");
        publisher = Publisher.newBuilder(topicName).build();
        event.getServletContext().setAttribute("publisher", publisher);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
// [END background_context_listener]
