package com.example.std.gettingstarted.controllers;

import com.example.std.gettingstarted.pubsub.MessagesService;
import com.example.std.gettingstarted.requests.SubscriberRequest;
import com.google.api.services.pubsub.model.PubsubMessage;
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
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class PubSubController
{
    private static final Logger log = LoggerFactory.getLogger(PubSubController.class);

    public static final String ASYNC_ENDPOINT = "/messages/async";

    @Autowired
    private MessagesService messagesService;

    @ApiOperation(value = "val",
                produces = "application/json",
                httpMethod = "GET", response = List.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Invalid data"),
    })
    @RequestMapping(value = "/messages", method = GET, produces = "application/json")
    public ResponseEntity showMessages() {
        List<String> messages = messagesService.getAllMessages();
        return new ResponseEntity(messages, HttpStatus.OK);
    }


    @RequestMapping(value = "/register/callback", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/json")
    public ResponseEntity registerInterestedParty(@RequestBody SubscriberRequest request){
        try {

            log.info("request = "+ request.toString());
            messagesService.createAsyncCallbackURLForTopic(request.getCallback(),
                                                            request.getTopic(),
                                                            request.getSubscriber());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/messages/async/other", method = POST, produces = "application/json")
    public ResponseEntity receiveAsyncMessage(@RequestBody PubsubMessage map){

        try {
            messagesService.receiveMessage(map);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = ASYNC_ENDPOINT, method = POST, produces = "application/json")
    public ResponseEntity receiveMessage(@RequestBody PubsubMessage map){

        try {
            messagesService.receiveMessage(map);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/send/{topic}/{message}", method = GET, produces = "application/json")
    public ResponseEntity sendMessage(@PathVariable String topic, @PathVariable String message){

        try {
            messagesService.sendMessage(topic,message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity(HttpStatus.OK);
    }





}
