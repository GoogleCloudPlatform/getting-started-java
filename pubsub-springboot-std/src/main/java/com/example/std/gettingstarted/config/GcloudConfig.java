package com.example.std.gettingstarted.config;

import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.apphosting.api.ApiProxy;
import com.travellazy.google.pubsub.service.CallbackHook;
import com.travellazy.google.pubsub.service.MessageAPI;
import com.travellazy.google.pubsub.service.MessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;


@Configuration
public class GcloudConfig
{
    @Value("${appengine.projectId}")
    private String projectId;

    private String deploymentUrl;

    @Bean
    @ConditionalOnMissingBean
    public CallbackHook buildReceiveMessageCallback(){
        return new ReceiveMessageCallback();
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageService getMessagesService(CallbackHook callbackHook){
        return MessageAPI.getMessageFactory(projectId,callbackHook).getMessageService();
    }

    @PostConstruct
    public void init() {
        setProjectIdAndDeploymentUrl();
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

