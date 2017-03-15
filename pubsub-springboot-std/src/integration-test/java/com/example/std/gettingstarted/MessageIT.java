package com.example.std.gettingstarted;/**
 * Author: wge
 * Date: 03/11/2016
 * Time: 20:12
 */

import com.example.std.gettingstarted.config.CoreConnection;
import com.example.std.gettingstarted.config.TestConfig;
import com.travellazy.google.pubsub.service.MessageService;
import com.travellazy.google.pubsub.util.TopicValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class MessageIT extends GAETest {

    @Autowired
    MessageService messageService;

    @Autowired
    TestConfig testConfig;

    @Test
    public void testVersion() throws Exception {
        String sr = fireTextHtmlGET(CoreConnection.buildUrl("/api/version"));
        assertTrue(sr.contains("0.0.1-SNAPSHOT"));
    }

    @Test
    public void createTopic() throws Exception {


        TopicValue topic = messageService.createOrFindTopic("testTopic");
        assertEquals("testTopic", topic.getTopicKey());

        messageService.createSubscription(topic, "sub1", CoreConnection.buildUrl("/messages/async"));

        assertEquals(1, messageService.getAllSubscriptions().size());
        assertTrue(messageService.getAllSubscriptions().contains("sub1"));

        messageService.broadcastMessage("testTopic", "messageContent");

        assertNotNull(testConfig.actualMessage);
    }



}
