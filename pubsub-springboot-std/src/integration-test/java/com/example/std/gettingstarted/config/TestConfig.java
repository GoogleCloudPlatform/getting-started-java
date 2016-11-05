package com.example.std.gettingstarted.config;/**
 * Author: wge
 * Date: 03/11/2016
 * Time: 22:31
 */

import com.example.std.gettingstarted.DefaultLocalMessagesService;
import com.example.std.gettingstarted.pubsub.MessagesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class TestConfig {
    private static final Logger log = LoggerFactory.getLogger(Config.class);

    public TestConfig(){
        log.info("using test config") ;
    }

    @Bean
    public MessagesService buildMessagesService() {
        return new DefaultLocalMessagesService();
    }


}
