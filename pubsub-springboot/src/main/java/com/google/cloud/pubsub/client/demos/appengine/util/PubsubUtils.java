/*
 * Copyright (c) 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.google.cloud.pubsub.client.demos.appengine.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.pubsub.Pubsub;
import com.google.api.services.pubsub.PubsubScopes;
import com.google.common.base.Preconditions;

import java.io.IOException;

public final class PubsubUtils {

    /**
     * The application name will be attached to the API requests.
     */
    private final Pubsub client;

    public PubsubUtils(final String APPLICATION_NAME) {
        try {
            client = createClient(APPLICATION_NAME);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Pubsub getClient(){
        return client;
    }
    /**
     * Builds a new Pubsub client with default HttpTransport and
     * JsonFactory and returns it.
     *
     * @return Pubsub client.
     * @throws IOException when we can not get the default credentials.
     */
    private static Pubsub createClient(final String APPLICATION_NAME) throws IOException {
        return findOrCreateClient(APPLICATION_NAME,
                Utils.getDefaultTransport(),
                Utils.getDefaultJsonFactory());
    }

    private static Pubsub findOrCreateClient(final String APPLICATION_NAME,
            final HttpTransport httpTransport,
                                             final JsonFactory jsonFactory)
            throws IOException {
        Preconditions.checkNotNull(httpTransport);
        Preconditions.checkNotNull(jsonFactory);
        GoogleCredential credential = GoogleCredential.getApplicationDefault();
        if (credential.createScopedRequired()) {
            credential = credential.createScoped(PubsubScopes.all());
        }
        // Please use custom HttpRequestInitializer for automatic
        // retry upon failures.
        HttpRequestInitializer initializer = new RetryHttpInitializerWrapper(credential);
        return new Pubsub.Builder(httpTransport, jsonFactory, initializer)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
