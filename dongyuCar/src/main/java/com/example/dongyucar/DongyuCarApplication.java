package com.example.dongyucar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class DongyuCarApplication {

    public static void main(String[] args) {
        SpringApplication.run(DongyuCarApplication.class, args);
    }

}
