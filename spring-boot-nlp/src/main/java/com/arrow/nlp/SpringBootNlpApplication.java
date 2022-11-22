package com.arrow.nlp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,GsonAutoConfiguration.class})
public class SpringBootNlpApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootNlpApplication.class, args);
    }

}
