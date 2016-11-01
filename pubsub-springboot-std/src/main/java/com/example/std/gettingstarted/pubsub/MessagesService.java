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
    List<String> getAllMessages();

    void receiveMessage(PubsubMessage message) throws IOException;

    void sendMessage(String topicKey, String message) throws IOException, NoTopicFoundException;

    /**
     *
     * @param topicKey the name of the topic to be created or found
     * @return  the immutable Topic object encapsulating the topic with its full url
     * @throws IOException
     */
    TopicValue createOrFindTopic(String topicKey) throws IOException;

    SubscriptionValue createSubscription(TopicValue topicValue, String subscriptionKey, String urlCallback) throws IOException;

    Collection<String> getAllTopics() throws IOException;

    Collection<String> getAllSubscriptions() throws IOException;
}
