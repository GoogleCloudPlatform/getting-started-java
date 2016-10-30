package com.example.std.gettingstarted.pubsub;

import com.example.std.gettingstarted.exceptions.NoTopicFoundException;
import com.google.api.client.util.Base64;
import com.google.api.services.pubsub.Pubsub;
import com.google.api.services.pubsub.model.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.travellazy.google.pubsub.util.GCloudClientPubSub;
import com.travellazy.google.pubsub.util.SubscriptionValue;
import com.travellazy.google.pubsub.util.TopicValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static com.google.appengine.api.datastore.Query.SortDirection.DESCENDING;

public class DefaultMessagesService implements MessagesService {
    private static final Logger log = LoggerFactory.getLogger(DefaultMessagesService.class);
    private final GCloudClientPubSub client;

    public DefaultMessagesService(final GCloudClientPubSub client) {
        this.client = client;
    }

    @Override
    public List<String> getAllMessages() {
        MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        List<String> messages = (List<String>) memcacheService.get(Constants.MESSAGE_CACHE_KEY);
        if (messages == null) {
            messages = new ArrayList<>();
            // If no messages in the memcache, look for the datastore
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            PreparedQuery query = datastore.prepare(new Query("PubsubMessage").addSort("receipt-time", DESCENDING));

            for (Entity entity : query.asIterable(FetchOptions.Builder.withLimit(Constants.MAX_COUNT))) {
                String message = (String) entity.getProperty("message");
                messages.add(message);
            }
            // Store them to the memcache for future use.
            memcacheService.put(Constants.MESSAGE_CACHE_KEY, messages);
        }
        return messages;
    }

    @Override
    public void receiveMessage(PubsubMessage pubsubMessage) throws IOException {

        log.info("rawMessage = " + pubsubMessage.toPrettyString());

        Map<String, Object> map = (Map) pubsubMessage.get("message");

        Set<String> keys = map.keySet();
        for (String k : keys) {
            log.info("key,val =" + k + " " + map.get(k));
        }

        String encodedData = (String) map.get("data");
        log.info("dencodedData = " + encodedData);
        byte[] rawData = Base64.decodeBase64(encodedData);
        String decodedData = new String(rawData, "UTF-8");
        log.info("byte[] decoded from pubsubMessage = " + pubsubMessage.decodeData());
        log.info("received message as " + decodedData);
    }


    @Override
    public void sendMessage(String topicName, String message) throws IOException, NoTopicFoundException {
        client.sendMessage(topicName, message);
    }

    @Override
    public TopicValue createOrFindTopic(String topicKey) throws IOException {
        return client.createTopic(topicKey);
    }

    @Override
    public SubscriptionValue createSubscription(TopicValue topicValue, String subscriptionName, String urlCallback) throws IOException {
        return client.createSubscriptionForTopic(topicValue, subscriptionName, urlCallback);
    }

    @Override
    public Collection<String> getAllTopics() throws IOException {
        List<String> topicNames = new ArrayList<>();
        Pubsub.Projects.Topics.List listMethod = client.listTopics();
        String nextPageToken = null;
        do {
            if (nextPageToken != null) {
                listMethod.setPageToken(nextPageToken);
            }
            ListTopicsResponse response = listMethod.execute();
            if (!response.isEmpty()) {
                for (Topic topic : response.getTopics()) {
                    topicNames.add(topic.getName());
                }
            }
            nextPageToken = response.getNextPageToken();
        } while (nextPageToken != null);

        return topicNames;
    }

    @Override
    public Collection<String> getAllSubscriptions() throws IOException {
        List<String> subscriptionNames = new ArrayList<>();
        Pubsub.Projects.Subscriptions.List listMethod = client.listSubscriptions();
        String nextPageToken = null;
        do {
            if (nextPageToken != null) {
                listMethod.setPageToken(nextPageToken);
            }
            ListSubscriptionsResponse response = listMethod.execute();
            if (!response.isEmpty()) {
                for (Subscription subscription : response.getSubscriptions()) {
                    subscriptionNames.add(subscription.toPrettyString());
                }
            }
            nextPageToken = response.getNextPageToken();
        } while (nextPageToken != null);


        return subscriptionNames;
    }


    private void receiveMessages() throws IOException {
        // Validating unique subscription token before processing the message
        String subscriptionToken = System.getProperty(Constants.BASE_PACKAGE + ".subscriptionUniqueToken");
        //            if (!subscriptionToken.equals(req.getParameter("token"))) {
        //                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        //                resp.getWriter().close();
        //                return;
        //            }

        //ServletInputStream inputStream = req.getInputStream();

        // Parse the JSON message to the POJO model class
        //JsonParser parser = JacksonFactory.getDefaultInstance().createJsonParser(inputStream);
        //            parser.skipToKey("message");
        //            PubsubMessage message = parser.parseAndClose(PubsubMessage.class);

        // Store the message in the datastore
        //            Entity messageToStore = new Entity("PubsubMessage");
        //            messageToStore.setProperty("message",new String(message.decodeData(), "UTF-8"));
        //            messageToStore.setProperty("receipt-time", System.currentTimeMillis());
        //            DatastoreService datastore =
        //                    DatastoreServiceFactory.getDatastoreService();
        //            datastore.put(messageToStore);
        //
        //            // Invalidate the cache
        //            MemcacheService memcacheService =
        //                    MemcacheServiceFactory.getMemcacheService();
        //            memcacheService.delete(Constants.MESSAGE_CACHE_KEY);

        // Acknowledge the message by returning a success code
        //        }
    }

}
