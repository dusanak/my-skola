package com.skillsfighters.runnable;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static com.skillsfighters.security.FirebaseUtils.checkAndInitializeFirebase;

@ComponentScan(basePackages = "com.skillsfighters")
@Configuration
@SpringBootApplication
@EntityScan(basePackages = {"com.skillsfighters.domain"})
@EnableJpaRepositories(basePackages = {"com.skillsfighters.repository"})
@Slf4j
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        checkAndInitializeFirebase();
    }
}

