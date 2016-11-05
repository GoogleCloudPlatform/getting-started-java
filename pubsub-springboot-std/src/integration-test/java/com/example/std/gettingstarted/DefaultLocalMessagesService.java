package com.example.std.gettingstarted;/**
 * Author: wge
 * Date: 03/11/2016
 * Time: 22:47
 */

import com.example.std.gettingstarted.pubsub.MessagesService;
import com.google.api.services.pubsub.model.PubsubMessage;
import com.travellazy.google.pubsub.util.State;
import com.travellazy.google.pubsub.util.SubscriptionValue;
import com.travellazy.google.pubsub.util.TopicValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultLocalMessagesService implements MessagesService {

    private Map<String, TopicValue> topicKeyToTopicValueMap = new ConcurrentHashMap<>();
    private Map<TopicValue, Set<String>> subscribers = new ConcurrentHashMap<>();
    private Map<String, String> subscriber2EndpointMap = new ConcurrentHashMap<>();
    private Logger log = LoggerFactory.getLogger(DefaultLocalMessagesService.class);

    @Override
    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    private MessageSender messageSender;

    @Override
    public List<String> getAllMessages() {
        return Collections.EMPTY_LIST;
    }

    private CallbackHook callback;

    @Override
    public void receiveMessage(PubsubMessage message) throws IOException {
        //ignore ?
        callback.recieveMessage(message);
    }

    private IdGenerator idGenerator = new IdGenerator();

    class IdGenerator {
        public String next() {
            return UUID.randomUUID().toString().replace("-", "");
        }
    }

    @Override
    public void sendMessage(String topicKey, String message) throws Exception {
        TopicValue topicValue = new TopicValue("projects/myprojectId/topics", topicKey, State.CREATED);
        Set<String> subscribers = this.subscribers.get(topicValue);
        for (String subscriber : subscribers) {

            String endpoint = subscriber2EndpointMap.get(subscriber);

            log.info("firing message " + message + " to " + endpoint);
            PubsubMessage pubsubMsg = new PubsubMessage();
            pubsubMsg.setMessageId(idGenerator.next());
            pubsubMsg.setPublishTime(System.currentTimeMillis() + "");
            pubsubMsg.setData(message);

            messageSender.sendPublishMessage(endpoint, pubsubMsg);
        }
    }

    @Override
    public TopicValue createOrFindTopic(String topicKey) throws IOException {
        TopicValue topicValue = new TopicValue("projects/myprojectId/topics", topicKey, State.ALREADY_EXISTS);
        TopicValue found = topicKeyToTopicValueMap.get(topicKey);
        if (found == null) {
            topicKeyToTopicValueMap.put(topicKey, topicValue);
            found = topicValue;
        }
        return found;
    }

    @Override
    public SubscriptionValue createSubscription(TopicValue topicValue, String subscriptionKey, String urlCallback) throws IOException {
        this.subscriber2EndpointMap.put(subscriptionKey, urlCallback);
        Set<String> subsciberKeys = this.subscribers.get(topicValue);
        State state;
        if (subsciberKeys == null) {
            subsciberKeys = new HashSet<>();
            state = State.CREATED;
        } else {
            state = State.ALREADY_EXISTS;
        }


        subsciberKeys.add(subscriptionKey);
        subscribers.put(topicValue, subsciberKeys);


        return new SubscriptionValue(subscriptionKey, urlCallback, topicValue, state);
    }

    @Override
    public Collection<String> getAllTopics() throws IOException {
        return null;
    }

    @Override
    public Collection<String> getAllSubscriptions() throws IOException {
        return subscriber2EndpointMap.keySet();
    }

    @Override
    public void setCallbackHook(CallbackHook callbackHook) {
        this.callback = callbackHook;
    }
}