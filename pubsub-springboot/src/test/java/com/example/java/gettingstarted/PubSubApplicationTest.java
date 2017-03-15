/*
 * Copyright 2015 Google Inc. All Rights Reserved.
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

package com.example.java.gettingstarted;

import com.google.api.services.pubsub.model.PubsubMessage;
import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.google.cloud.pubsub.client.demos.appengine.PubSubApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.UnsupportedEncodingException;

public class PubSubApplicationTest {

  @Test
  public void publishMessageDecodes() throws UnsupportedEncodingException {

    PubsubMessage pubsubMessage = new PubsubMessage()
            .setMessageId("44239047666662")
            .setPublishTime(DateTime.now().getMillis() +"")
            .setData("dGVzdDI3");

    String decoded = new String(pubsubMessage.decodeData(),"UTF-8");
    Assert.assertEquals("test27",decoded);

  }

}
