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

package com.getstarted.background.functions;

import com.getstarted.background.objects.PubSubMessage;
import com.getstarted.background.objects.TranslateMessage;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translation;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
    name = "translate",
    urlPatterns = {"/", "/translate"})
public class TranslateServlet extends HttpServlet {
  private static final Gson gson = new Gson();
  private static final String PUBSUB_VERIFICATION_TOKEN =
      System.getenv("PUBSUB_VERIFICATION_TOKEN");

  // [START getting_started_background_app_list]
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    Firestore firestore = (Firestore) this.getServletContext().getAttribute("firestore");
    CollectionReference translations = firestore.collection("translations");
    QuerySnapshot snapshot;
    try {
      snapshot = translations.limit(10).get().get();
    } catch (InterruptedException | ExecutionException e) {
      throw new ServletException("Exception retrieving documents from Firestore.", e);
    }
    List<TranslateMessage> translateMessages = Lists.newArrayList();
    List<QueryDocumentSnapshot> documents = Lists.newArrayList(snapshot.getDocuments());
    documents.sort(Comparator.comparing(DocumentSnapshot::getCreateTime));

    for (DocumentSnapshot document : Lists.reverse(documents)) {
      String encoded = gson.toJson(document.getData());
      TranslateMessage message = gson.fromJson(encoded, TranslateMessage.class);
      message.setData(decode(message.getData()));
      translateMessages.add(message);
    }
    req.setAttribute("messages", translateMessages);
    req.setAttribute("page", "list");
    req.getRequestDispatcher("/base.jsp").forward(req, resp);
  }
  // [END getting_started_background_app_list]

  /**
   * Handle a posted message from Pubsub.
   *
   * @param req The message Pubsub posts to this process.
   * @param resp Not used.
   */
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {

    // Block requests that don't contain the proper verification token.
    String pubsubVerificationToken = PUBSUB_VERIFICATION_TOKEN;
    if (req.getParameter("token").compareTo(pubsubVerificationToken) != 0) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    // [START getting_started_background_translate_string]
    String body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

    PubSubMessage pubsubMessage = gson.fromJson(body, PubSubMessage.class);
    TranslateMessage message = pubsubMessage.getMessage();

    // Use Translate service client to translate the message.
    Translate translate = (Translate) this.getServletContext().getAttribute("translate");
    message.setData(decode(message.getData()));
    Translation translation =
        translate.translate(
            message.getData(),
            Translate.TranslateOption.sourceLanguage(message.getAttributes().getSourceLang()),
            Translate.TranslateOption.targetLanguage(message.getAttributes().getTargetLang()));
    // [END getting_started_background_translate_string]

    message.setTranslatedText(translation.getTranslatedText());

    try {
      // [START getting_started_background_translate]
      // Use Firestore service client to store the translation in Firestore.
      Firestore firestore = (Firestore) this.getServletContext().getAttribute("firestore");

      CollectionReference translations = firestore.collection("translations");

      ApiFuture<WriteResult> setFuture = translations.document().set(message, SetOptions.merge());

      setFuture.get();
      resp.getWriter().write(translation.getTranslatedText());
      // [END getting_started_background_translate]
    } catch (InterruptedException | ExecutionException e) {
      throw new ServletException("Exception storing data in Firestore.", e);
    }
  }

  private String decode(String data) throws UnsupportedEncodingException {
    return new String(Base64.getDecoder().decode(data), "UTF-8");
  }
}
