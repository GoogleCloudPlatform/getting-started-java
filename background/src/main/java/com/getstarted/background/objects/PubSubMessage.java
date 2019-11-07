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

package com.getstarted.background.objects;

// [START getting_started_background_pub_sub_message]
public class PubSubMessage {
  private TranslateMessage message;
  private String subscription;

  public TranslateMessage getMessage() {
    return message;
  }

  public void setMessage(TranslateMessage message) {
    this.message = message;
  }

  public String getSubscription() {
    return subscription;
  }

  public void setSubscription(String subscription) {
    this.subscription = subscription;
  }
}
// [END getting_started_background_pub_sub_message]
