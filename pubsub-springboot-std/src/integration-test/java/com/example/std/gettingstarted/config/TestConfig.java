package com.example.std.gettingstarted.config;/**
 * Author: wge
 * Date: 03/11/2016
 * Time: 22:31
 */

import com.example.std.gettingstarted.DefaultLocalMessageService;
import com.google.api.services.pubsub.model.PubsubMessage;
import com.travellazy.google.pubsub.service.CallbackHook;
import com.travellazy.google.pubsub.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class TestConfig implements CallbackHook {
    private static final Logger log = LoggerFactory.getLogger(Config.class);

    public TestConfig(){
        log.info("using test config") ;
    }

    public PubsubMessage actualMessage;

    @Override
    public void receiveMessage(PubsubMessage pubsubMessage) {
        actualMessage = pubsubMessage;
    }

    @Bean
    public MessageService buildMessagesService() {
        return new DefaultLocalMessageService(this);
    }


}
