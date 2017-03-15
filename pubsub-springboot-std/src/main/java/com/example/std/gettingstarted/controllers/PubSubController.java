package com.example.std.gettingstarted.controllers;

import com.example.std.gettingstarted.pubsub.TopicBean;
import com.example.std.gettingstarted.requests.SubscriberRequest;
import com.example.std.gettingstarted.responses.SubscriptionDisplay;
import com.example.std.gettingstarted.responses.TopicDisplay;
import com.google.api.services.pubsub.model.PubsubMessage;
import com.travellazy.google.pubsub.service.MessageService;
import com.travellazy.google.pubsub.util.SubscriptionValue;
import com.travellazy.google.pubsub.util.TopicValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class PubSubController {
    private static final Logger log = LoggerFactory.getLogger(PubSubController.class);
    public static final String ASYNC_ENDPOINT = "/messages/async";

    @Autowired
    private MessageService messageService;

//    @ApiOperation(value = "val", produces = "application/json", httpMethod = "GET", response = List.class)
//    @ApiResponses({
//            @ApiResponse(code = 200, message = "Invalid data"),
//    })
//    @RequestMapping(value = "/messages", method = GET, produces = "application/json")
//    public List<String> showMessages() {
//        return messageService.getAllMessages();
//    }


    @RequestMapping(value = "/register/callback", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionDisplay registerInterestedParty(@RequestBody SubscriberRequest request) throws IOException {

        log.info("request = " + request.toString());
        TopicValue topicValue = messageService.createOrFindTopic(request.getTopicKey());

        SubscriptionValue subscriptionValue = messageService.createSubscription(topicValue,
                request.getSubscriberKey(),
                request.getCallback());

        return new SubscriptionDisplay(subscriptionValue);
    }


    @RequestMapping(value = "/messages/async/other", method = POST, produces = "application/json")
    public void receiveAsyncMessage(@RequestBody PubsubMessage map) throws IOException {
        messageService.receiveMessage(map);
    }

    @RequestMapping(value = ASYNC_ENDPOINT, method = POST, produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public void receiveMessage(@RequestBody PubsubMessage pubsubMessage) throws IOException {
        log.info("heard message");
        messageService.receiveMessage(pubsubMessage);
    }

    @RequestMapping(value = "/topic", method = POST, produces = "application/json")
    public ResponseEntity createTopic(@RequestBody TopicBean topicBean) throws IOException {
        TopicValue topicValue = messageService.createOrFindTopic(topicBean.topicPrefix);
        HttpStatus status = topicValue.wasCreated() ? HttpStatus.CREATED : HttpStatus.OK;
        return new ResponseEntity(new TopicDisplay(topicValue), status);
    }

    @RequestMapping(value = "/topic", method = GET, produces = "application/json")
    public Collection<String> showTopics() throws IOException {
        return messageService.getAllTopics();
    }

    @RequestMapping(value = "/subscriptions", method = GET, produces = "application/json")
    public Collection<String> showSubscriptions() throws IOException {
        return messageService.getAllSubscriptions();
    }

    @RequestMapping(value = "/send/{topic}/{message}", method = GET, produces = "application/json")
    public void sendMessage(@PathVariable String topic, @PathVariable String message) throws Exception {
        messageService.broadcastMessage(topic, message);
    }
}
