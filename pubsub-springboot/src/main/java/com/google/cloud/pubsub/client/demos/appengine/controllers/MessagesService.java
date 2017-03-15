package com.google.cloud.pubsub.client.demos.appengine.controllers;

import com.google.api.services.pubsub.model.PubsubMessage;

import java.io.IOException;
import java.util.List;

public interface MessagesService {
    void createAsyncCallbackURLForTopic(String fullCallbackUrlEndpoint,
                                        String fullTopicName,
                                        String fullSubscriptionName) throws IOException;

    List<String> getAllMessages();

    void receiveMessage(PubsubMessage message) throws IOException;

    void sendMessage(String topic, String message) throws IOException;
}
