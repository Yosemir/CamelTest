package com.telefonica.camel;

import org.apache.camel.spring.boot.CamelAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { CamelAutoConfiguration.class })
public class NrtScoringApplication {

    public static void main( String[] args ) {
        SpringApplication.run(NrtScoringApplication.class, args);
    }

}
