package com.example.std.gettingstarted;/**
 * Author: wge
 * Date: 03/11/2016
 * Time: 20:12
 */

import com.example.std.gettingstarted.config.CoreConnection;
import com.example.std.gettingstarted.pubsub.MessagesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.pubsub.model.PubsubMessage;
import com.travellazy.google.pubsub.util.TopicValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class MessageIT extends GAETest implements CallbackHook, MessageSender {
    private static final Logger log = LoggerFactory.getLogger(MessageIT.class);

    @Autowired
    MessagesService messagesService;

    @Autowired
    RestTemplate restTemplate;

    @Test
    public void testVersion() throws Exception {
        String sr = fireGET(CoreConnection.buildUrl("/api/version"));
        assertTrue(sr.contains("0.7.5-SNAPSHOT"));
    }

    @Test
    public void createTopic() throws Exception {

        String result = restTemplate.getForObject("http://services.groupkt.com/country/get/all", String.class, new Object());
        log.info(result);
        messagesService.setCallbackHook(this);
        messagesService.setMessageSender(this);

        TopicValue topic = messagesService.createOrFindTopic("testTopic");
        assertEquals("testTopic",topic.getTopicKey());

        messagesService.createSubscription(topic,"sub1",CoreConnection.buildUrl("/messages/async"));

        assertEquals(2,messagesService.getAllSubscriptions().size());
        assertTrue(messagesService.getAllSubscriptions().contains("sub1"));

        messagesService.sendMessage("testTopic","messageContent");

    }


    @Override
    public void recieveMessage(PubsubMessage message) {
        System.out.println("received "  + message.toString());

    }

    @Test
    public void sendTestPost()  {
         ObjectMapper mapper = new ObjectMapper();

         Map<String,Object> map = new HashMap();
         map.put("question","Favourite programming language?");
         map.put("choices", Arrays.asList("Swift",
             "Python",
             "Objective-C",
             "Ruby"));
        String json = null;
        try {
            json = mapper.writeValueAsString(map);
            HttpHeaders headers = new HttpHeaders();
             headers.setContentType(MediaType.APPLICATION_JSON);
             HttpEntity<String> entity = new HttpEntity<>(json,headers);

             ResponseEntity<String> response =  restTemplate.postForEntity("http://polls.apiblueprint.org/questions",entity,String.class);
             log.info(response.getBody());

        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
     }

    @Override
    public void sendPublishMessage(String endpoint, PubsubMessage message) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> map = new HashMap();
        map.put("question","Favourite programming language?");
        map.put("choices", Arrays.asList("Swift",
            "Python",
            "Objective-C",
            "Ruby"));
        String json = mapper.writeValueAsString(map);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json,headers);

        ResponseEntity<String> response =  restTemplate.postForEntity("http://polls.apiblueprint.org/questions",entity,String.class);
        log.info(response.getBody());

    }

//    @Override
//    public void sendPublishMessage(String endpoint, PubsubMessage message) throws Exception {
//        ObjectMapper mapper = new ObjectMapper();
//         String pubMsgJsonStr = mapper.writeValueAsString(message);
//         log.info(pubMsgJsonStr);
//
//        firePOST(endpoint,pubMsgJsonStr);
//    }
}
