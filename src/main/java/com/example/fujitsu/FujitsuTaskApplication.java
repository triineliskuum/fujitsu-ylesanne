package com.example.fujitsu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FujitsuTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(FujitsuTaskApplication.class, args);
    }

}
