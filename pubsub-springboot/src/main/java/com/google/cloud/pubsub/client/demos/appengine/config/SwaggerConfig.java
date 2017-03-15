package com.google.cloud.pubsub.client.demos.appengine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
@ComponentScan(basePackages = "com.google.cloud.pubsub.client.demos.appengine.controllers")
public class SwaggerConfig {

    @Bean
    public Docket documentation() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(metadata())
                .select()
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public UiConfiguration uiConfig() {
        return UiConfiguration.DEFAULT;
    }

    private ApiInfo metadata() {
        return new ApiInfoBuilder()
                .title("itaxibook API")
                .description("description")
                .version("1.0")
                .contact("itaxibookprod@gmail.com")
                .build();
    }


}
