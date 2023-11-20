package com.fitness.gymsup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GymsupApplication {

    public static void main(String[] args) {
        SpringApplication.run(GymsupApplication.class, args);
    }

}
