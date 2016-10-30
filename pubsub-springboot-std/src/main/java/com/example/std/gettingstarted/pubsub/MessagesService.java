package com.example.std.gettingstarted.pubsub;

import com.google.api.services.pubsub.model.PubsubMessage;

import java.io.IOException;
import java.util.List;

public interface MessagesService
{
    void createAsyncCallbackURLForTopic(String fullCallbackUrlEndpoint, String fullTopicName, String fullSubscriptionName) throws IOException;

    List<String> getAllMessages();

    void receiveMessage(PubsubMessage message) throws IOException;

    void sendMessage(String topic, String message) throws IOException;

    /**
     *
     * @param topicName
     * @return  the full topic name created
     * @throws IOException
     */
    String createTopic(String topicName) throws IOException;
}
