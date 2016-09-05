package com.google.cloud.pubsub.client.demos.appengine.controllers;

import com.google.api.services.pubsub.model.PubsubMessage;
import com.google.api.services.pubsub.model.PushConfig;
import com.google.cloud.pubsub.client.demos.appengine.util.PubsubUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class PubSubController {

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
