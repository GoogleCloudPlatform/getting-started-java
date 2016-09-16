package com.example.std.gettingstarted.config;

import com.example.std.gettingstarted.util.IPLookupHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.io.IOException;



@Configuration
@EnableAutoConfiguration
public class Config {
    private static final Logger log = LoggerFactory.getLogger(Config.class);


    @Autowired
    private Environment env;

    @Value("${app.url}")
    public String appUrl;

    @Value("${server.port}")
    public String port;

    @Value("${app.env}")
    public String appEnv;

    @Value("${app.version}")
    public String MAVEN_VERSION;

    private static String environment = "LOCAL";


    @Bean
    public javax.validation.Validator buildValidator() {
        return new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean();
    }


    @PostConstruct
    public void start() {

        log.info("Running at location " + getAppUrl());

        String deployEnv = env.getProperty("DEPLOY_ENV");
        log.info("DEPLOY_ENV = " + deployEnv);

        CoreConnection.maven_version = MAVEN_VERSION;

        if (deployEnv != null && deployEnv.equalsIgnoreCase("PROD")) {
            CoreConnection.appUrl = appUrl;
            CoreConnection.port = "";
        }

        log.info("=============================================");
        log.info(asString());
        log.info("=============================================");
    }




    private static String asString() {
        return (" env = " + environment) + " maven_version = " + CoreConnection.maven_version + " appUrl  = " + CoreConnection.appUrl;
    }



    private String getAppUrl(){

        String hostUrl;
        String environment = System.getProperty("com.google.appengine.runtime.environment");
        if (StringUtils.equals("Production", environment)) {
            String applicationId = System.getProperty("com.google.appengine.application.id");
            String version = System.getProperty("com.google.appengine.application.version");
            hostUrl = "http://"+version+"."+applicationId+".appspot.com";
        } else {
            try {
                hostUrl = "http://"+ IPLookupHelper.determinePublicIP() +":" + port;
            } catch (IOException e) {
                throw new RuntimeException("cant determine IP address");
            }
        }

        return hostUrl;

    }



}
