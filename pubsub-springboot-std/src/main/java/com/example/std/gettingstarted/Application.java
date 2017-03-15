package com.example.std.gettingstarted;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableSwagger2
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.std.gettingstarted.config",
                              "com.example.std.gettingstarted.controllers"
                                })
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }




}
