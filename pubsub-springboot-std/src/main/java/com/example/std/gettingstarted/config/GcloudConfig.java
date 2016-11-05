package com.example.std.gettingstarted.config;

import com.example.std.gettingstarted.pubsub.DefaultMessagesService;
import com.example.std.gettingstarted.pubsub.MessagesService;
import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.apphosting.api.ApiProxy;
import com.travellazy.google.pubsub.util.GCloudClientPubSub;
import com.travellazy.google.pubsub.util.GloudPubSubClientWrapper;
import com.travellazy.google.pubsub.util.SubscriptionValue;
import com.travellazy.google.pubsub.util.TopicValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;

import static com.example.std.gettingstarted.controllers.PubSubController.ASYNC_ENDPOINT;


@Configuration
public class GcloudConfig
{
    private static final Logger log = LoggerFactory.getLogger(GcloudConfig.class);

    @Value("${appengine.projectId}")
    private String projectId;

    private String pushEndpoint;
    private String deploymentUrl;

    @Autowired
    MessagesService messagesService;


    @Bean
    @ConditionalOnMissingBean
    public MessagesService buildMessagesService(GCloudClientPubSub pubsub) {
        return new DefaultMessagesService(pubsub);
    }

    @Bean
    public GCloudClientPubSub getPubSub(){
        return new GloudPubSubClientWrapper(projectId);
    }

    @PostConstruct
    public void init() {
        setProjectIdAndDeploymentUrl();

        pushEndpoint = deploymentUrl + ASYNC_ENDPOINT;

        //create the first topic and subscription to it.
        try {
            TopicValue fullTopic = messagesService.createOrFindTopic("topic-pubsub-api-appengine-sample");
            log.info("fullTopic = " + fullTopic.toString());

            SubscriptionValue subscriptionValue = messagesService.createSubscription(fullTopic,"subscription-" + projectId,pushEndpoint);
            log.info("subsciption = " + subscriptionValue.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setProjectIdAndDeploymentUrl(){
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

