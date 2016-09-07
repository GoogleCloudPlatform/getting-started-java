package com.google.cloud.pubsub.client.demos.appengine.config;

import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.apphosting.api.ApiProxy;
import com.google.cloud.pubsub.client.demos.appengine.controllers.DefaultMessagesService;
import com.google.cloud.pubsub.client.demos.appengine.controllers.MessagesService;
import com.travellazy.google.pubsub.util.GCloudClientPubSub;
import com.travellazy.google.pubsub.util.GloudPubSubClientWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;

import static com.google.cloud.pubsub.client.demos.appengine.controllers.PubSubController.ASYNC_ENDPOINT;

@Configuration
public class BeanConfig {

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
    public MessagesService buildMessagesService(GCloudClientPubSub pubsub,TopicBean topicBean) {
        return new DefaultMessagesService(pubsub,topicBean);
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
//        String url = getEnv();
//        if (!url.contains("localhost")) {
            AppIdentityService identityService = AppIdentityServiceFactory.getAppIdentityService();
            // The project ID associated to an app engine application is the same as the app ID.
            projectId = identityService.parseFullAppId(ApiProxy.getCurrentEnvironment().getAppId()).getId();
            deploymentUrl = "https://" + projectId + ".appspot.com";
//        }
//        else{
//            deploymentUrl = "http://localhost:8080";
//
//        }

        pushEndpoint = deploymentUrl + ASYNC_ENDPOINT;

        final String topicPrefix = "projects/"+ projectId + "/topics/";
        topicBean.topicPrefix = topicPrefix;

        fullTopicName = topicPrefix + APPLICATION_TOPIC_NAME;
        fullSubscriptionName = "projects/" + projectId + "/subscriptions/subscription-" + projectId;


        System.out.println("=========================");
        System.out.println("fullTopicName = " + fullTopicName);//projects/quixotic-tesla-142120/topics/topic-pubsub-api-appengine-sample
        System.out.println("fullSubscriptionName = " + fullSubscriptionName);
        System.out.println("=========================");

        //create the first.
        try {
            messagesService.createAsyncCallbackURLForTopic(pushEndpoint,fullTopicName,fullSubscriptionName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

//    private static String getEnv() {
//        String hostUrl;
//        String environment = System.getProperty("com.google.appengine.runtime.environment");
//        if ("Production".equals(environment)) {
//            String applicationId = System.getProperty("com.google.appengine.application.id");
//            String version = System.getProperty("com.google.appengine.application.version");
//            hostUrl = "http://" + version + "." + applicationId + ".appspot.com/";
//        } else {
//            hostUrl = "http://localhost:8888";
//        }
//        return hostUrl;
//    }




}
