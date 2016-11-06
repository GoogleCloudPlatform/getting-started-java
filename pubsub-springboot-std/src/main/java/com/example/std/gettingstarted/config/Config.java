package com.example.std.gettingstarted.config;

import com.example.std.gettingstarted.util.IPLookupHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.example.std.gettingstarted.config.CoreConnection.appUrl;


@Configuration
//@EnableAutoConfiguration
public class Config {
    private static final Logger log = LoggerFactory.getLogger(Config.class);

    @Autowired
    private Environment env;

    @Value("${server.port}")
    public String port;

    @Value("${env.app.version}")
    public String MAVEN_VERSION;

    private static String environment = "LOCAL";

    @Bean
    public javax.validation.Validator buildValidator() {
        return new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean();
    }

    @Bean
    public RestTemplate buildRestTemplate(){
        return new RestTemplate();
    }

    @Autowired
    RestTemplate restTemplate;

    @PostConstruct
    public void start() {

        String url = "https://jsonplaceholder.typicode.com/posts/1";
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        // Make the HTTP GET request, marshaling the response to a String
        String result = restTemplate.getForObject("http://services.groupkt.com/country/get/all", String.class, new Object());

        log.info("result ====" + result);

//        sendTestPost();

        log.info("Running at location " + getAppUrl());

        String deployEnv = env.getProperty("DEPLOY_ENV");
        log.info("DEPLOY_ENV = " + deployEnv);

        CoreConnection.maven_version = MAVEN_VERSION;

        if (deployEnv != null && deployEnv.equalsIgnoreCase("PROD")) {
            CoreConnection.port = "";
        }

        log.info("=============================================");
        log.info(asString());
        log.info("=============================================");
    }

    private static String asString() {
        return (" env = " + environment) + " maven_version = " + CoreConnection.maven_version + " appUrl  = " + appUrl;
    }

    private String getAppUrl(){

        String hostUrl;
        String environment = System.getProperty("com.google.appengine.runtime.environment");
        if ("Production".equalsIgnoreCase(environment)) {
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

    private void sendTestPost()  {
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
}
