package com.example.std.gettingstarted.config;

import com.example.std.gettingstarted.pubsub.MessagesService;
import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.apphosting.api.ApiProxy;
import com.example.std.gettingstarted.pubsub.DefaultMessagesService;
import com.example.std.gettingstarted.pubsub.TopicBean;
import com.travellazy.google.pubsub.util.GCloudClientPubSub;
import com.travellazy.google.pubsub.util.GloudPubSubClientWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;

import static com.example.std.gettingstarted.controllers.PubSubController.ASYNC_ENDPOINT;


@Configuration
public class GcloudConfig
{
    private static final Logger log = LoggerFactory.getLogger(GcloudConfig.class);

    //BASE_PACKAGE = "com.google.cloud.pubsub.client.demos.appengine"

    @Value("${appengine.projectId}")
    private String projectId;

    private static final String APPLICATION_NAME = "google-cloud-pubsub-appengine-sample/1.0";
    private static final String APPLICATION_TOPIC_NAME = "topic-pubsub-api-appengine-sample";

    private String pushEndpoint;
    private String deploymentUrl;
    private String fullTopicName;
    private String fullSubscriptionName;

    @Autowired
    MessagesService messagesService;


    @Bean
    public MessagesService buildMessagesService(GCloudClientPubSub pubsub, TopicBean topicBean) {
        return new DefaultMessagesService(pubsub, topicBean);
    }

    private TopicBean topicBean = new TopicBean();

    @Bean
    public GCloudClientPubSub getPubSub(){
        return new GloudPubSubClientWrapper(APPLICATION_NAME);
    }

    @Bean
    public TopicBean buildTopicBean(){
        return topicBean;
    }

    @PostConstruct
    public void init() {
        String url = getEnv();
        if (!url.contains("localhost")) {
            AppIdentityService identityService = AppIdentityServiceFactory.getAppIdentityService();
            if(identityService != null && ApiProxy.getCurrentEnvironment() !=null){
                projectId = identityService.parseFullAppId(ApiProxy.getCurrentEnvironment().getAppId()).getId();
                deploymentUrl = "https://" + projectId + ".appspot.com";
            }
            // The project ID associated to an app engine application is the same as the app ID.
        }
        else{
            deploymentUrl = "http://localhost:8080";
        }

        pushEndpoint = deploymentUrl + ASYNC_ENDPOINT;

        final String topicPrefix = "projects/"+ projectId + "/topics/";
        topicBean.topicPrefix = topicPrefix;

        fullTopicName = topicPrefix + APPLICATION_TOPIC_NAME;
        fullSubscriptionName = "projects/" + projectId + "/subscriptions/subscription-" + projectId;


        log.info("=========================");
        log.info("fullTopicName = " + fullTopicName);//projects/quixotic-tesla-142120/topics/topic-pubsub-api-appengine-sample
        log.info("fullSubscriptionName = " + fullSubscriptionName);
        log.info("pushEndpoint = " + pushEndpoint);
        log.info("=========================");

        //create the first.
        try {
            messagesService.createAsyncCallbackURLForTopic(pushEndpoint,fullTopicName,fullSubscriptionName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static String getEnv() {
        String hostUrl;
        String environment = System.getProperty("com.google.appengine.runtime.environment");
        if ("Production".equals(environment)) {
            String applicationId = System.getProperty("com.google.appengine.application.id");
            String version = System.getProperty("com.google.appengine.application.version");
            hostUrl = "http://" + version + "." + applicationId + ".appspot.com/";
        } else {
            hostUrl = "http://localhost:8080";
        }
        return hostUrl;
    }
}

