package com.example.std.gettingstarted.controllers;

import com.example.std.gettingstarted.pubsub.MessagesService;
import com.example.std.gettingstarted.pubsub.TopicBean;
import com.example.std.gettingstarted.requests.SubscriberRequest;
import com.example.std.gettingstarted.responses.SubscriptionDisplay;
import com.example.std.gettingstarted.responses.TopicDisplay;
import com.google.api.services.pubsub.model.PubsubMessage;
import com.travellazy.google.pubsub.util.SubscriptionValue;
import com.travellazy.google.pubsub.util.TopicValue;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class PubSubController {
    private static final Logger log = LoggerFactory.getLogger(PubSubController.class);
    private static final ResponseEntity OK = new ResponseEntity(HttpStatus.OK);
    public static final String ASYNC_ENDPOINT = "/messages/async";

    @Autowired
    private MessagesService messagesService;

    @ApiOperation(value = "val", produces = "application/json", httpMethod = "GET", response = List.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Invalid data"),
    })
    @RequestMapping(value = "/messages", method = GET, produces = "application/json")
    public ResponseEntity showMessages() {
        List<String> messages = messagesService.getAllMessages();
        return new ResponseEntity(messages, HttpStatus.OK);
    }


    @RequestMapping(value = "/register/callback", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/json")
    public ResponseEntity registerInterestedParty(@RequestBody SubscriberRequest request) throws IOException {

        log.info("request = " + request.toString());
        TopicValue topicValue = messagesService.createOrFindTopic(request.getTopicKey());

        SubscriptionValue subscriptionValue = messagesService.createSubscription(topicValue,
                request.getSubscriberKey(),
                request.getCallback());

        SubscriptionDisplay subscriptionDisplay = new SubscriptionDisplay(subscriptionValue);

        return new ResponseEntity(subscriptionDisplay, HttpStatus.OK);
    }


    @RequestMapping(value = "/messages/async/other", method = POST, produces = "application/json")
    public ResponseEntity receiveAsyncMessage(@RequestBody PubsubMessage map) throws IOException {
        messagesService.receiveMessage(map);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = ASYNC_ENDPOINT, method = POST, produces = "application/json")
    public ResponseEntity receiveMessage(@RequestBody PubsubMessage pubsubMessage) throws IOException {
        log.info("heard message");
        messagesService.receiveMessage(pubsubMessage);
        return OK;
    }

    @RequestMapping(value = "/topic", method = POST, produces = "application/json")
    public ResponseEntity createTopic(@RequestBody TopicBean topicBean) throws IOException {
        TopicValue topicValue = messagesService.createOrFindTopic(topicBean.topicPrefix);
        HttpStatus status = topicValue.wasCreated() ? HttpStatus.CREATED : HttpStatus.OK;
        return new ResponseEntity(new TopicDisplay(topicValue), status);
    }

    @RequestMapping(value = "/topic", method = GET, produces = "application/json")
    public ResponseEntity showTopics() throws IOException {
        Collection<String> topicValues = messagesService.getAllTopics();
        return new ResponseEntity(topicValues, HttpStatus.OK);
    }

    @RequestMapping(value = "/subscriptions", method = GET, produces = "application/json")
    public ResponseEntity showSubscriptions() throws IOException {
        Collection<String> subscriptions = messagesService.getAllSubscriptions();
        return new ResponseEntity(subscriptions, HttpStatus.OK);
    }



    @RequestMapping(value = "/send/{topic}/{message}", method = GET, produces = "application/json")
    public ResponseEntity sendMessage(@PathVariable String topic, @PathVariable String message) throws Exception {
        messagesService.sendMessage(topic, message);
        return OK;
    }
}
