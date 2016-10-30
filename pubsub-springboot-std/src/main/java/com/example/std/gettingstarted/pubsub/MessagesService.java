package com.example.std.gettingstarted.pubsub;

import com.example.std.gettingstarted.exceptions.NoTopicFoundException;
import com.google.api.services.pubsub.model.PubsubMessage;
import com.travellazy.google.pubsub.util.SubscriptionValue;
import com.travellazy.google.pubsub.util.TopicValue;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface MessagesService
{
//    void createAsyncCallbackURLForTopic(String fullCallbackUrlEndpoint, String fullTopicName, String fullSubscriptionName) throws IOException;

    List<String> getAllMessages();

    void receiveMessage(PubsubMessage message) throws IOException;

    void sendMessage(String topicKey, String message) throws IOException, NoTopicFoundException;

    /**
     *
     * @param topicName
     * @return  the full topic name created
     * @throws IOException
     */
    TopicValue createOrFindTopic(String topicName) throws IOException;

    SubscriptionValue createSubscription(TopicValue topicValue, String subscriptionKey, String urlCallback) throws IOException;

    Collection<String> getAllTopics() throws IOException;

    Collection<String> getAllSubscriptions() throws IOException;
}
