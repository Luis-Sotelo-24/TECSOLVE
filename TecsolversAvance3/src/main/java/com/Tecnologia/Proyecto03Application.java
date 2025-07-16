package com.Tecnologia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Proyecto03Application {

    public static void main(String[] args) {
        SpringApplication.run(Proyecto03Application.class, args);
    }
}