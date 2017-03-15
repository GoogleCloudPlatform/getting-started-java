package com.example.std.gettingstarted;/**
 * Author: wge
 * Date: 03/11/2016
 * Time: 22:47
 */

import com.example.std.gettingstarted.util.IdGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.pubsub.model.PubsubMessage;
import com.travellazy.google.pubsub.util.State;
import com.travellazy.google.pubsub.util.SubscriptionValue;
import com.travellazy.google.pubsub.util.TopicValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultLocalMessageService implements com.travellazy.google.pubsub.service.MessageService {

    private Map<String, TopicValue> topicKeyToTopicValueMap = new ConcurrentHashMap<>();
    private Map<TopicValue, Set<String>> subscribers = new ConcurrentHashMap<>();
    private Map<String, String> subscriber2EndpointMap = new ConcurrentHashMap<>();
    private Logger log = LoggerFactory.getLogger(DefaultLocalMessageService.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final HttpHeaders headers = new HttpHeaders();
    private ObjectMapper mapper = new ObjectMapper();
    private IdGenerator idGenerator = new IdGenerator();

    public DefaultLocalMessageService(com.travellazy.google.pubsub.service.CallbackHook callback) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        this.callback = callback;
    }



    private com.travellazy.google.pubsub.service.CallbackHook callback;

    @Override
    public void receiveMessage(PubsubMessage message)  {
        callback.receiveMessage(message);
    }


    @Override
    public void broadcastMessage(String topicKey, String message) throws IOException {
        TopicValue topicValue = new TopicValue("projects/myprojectId/topics", topicKey, State.CREATED);
        Set<String> subscribers = this.subscribers.get(topicValue);
        for (String subscriber : subscribers) {

            String endpoint = subscriber2EndpointMap.get(subscriber);

            log.info("firing message " + message + " to " + endpoint);
            PubsubMessage pubsubMsg = new PubsubMessage();
            pubsubMsg.setMessageId(idGenerator.next());
            pubsubMsg.setPublishTime(System.currentTimeMillis() + "");
            pubsubMsg.setData(message);

            sendPublishMessage(endpoint,pubsubMsg);
        }
    }




    private ResponseEntity<String> sendPublishMessage(String endpoint, PubsubMessage message) throws JsonProcessingException {
         String json = mapper.writeValueAsString(message);
         HttpEntity<String> entity = new HttpEntity<>(json, headers);
         return restTemplate.postForEntity(endpoint, entity, String.class);
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
        return topicKeyToTopicValueMap.keySet();
    }

    @Override
    public Collection<String> getAllSubscriptions() throws IOException {
        return subscriber2EndpointMap.keySet();
    }


}