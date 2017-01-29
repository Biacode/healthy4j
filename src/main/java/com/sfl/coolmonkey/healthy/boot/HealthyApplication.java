package com.sfl.coolmonkey.healthy.boot;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

@PropertySource(value = "classpath:application.properties")
@PropertySource(value = "file:${user.home}/coolmonkey-healthyms.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${user.home}/coolmonkey/coolmonkey-healthyms.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${user.home}/coolmonkey-commons.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${user.home}/coolmonkey/coolmonkey-commons.properties", ignoreResourceNotFound = true)
@SpringBootApplication
@ImportResource({"classpath:applicationContext-boot.xml"})
public class HealthyApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthyApplication.class, args);
    }

    @Bean
    public Client jerseyClient() {
        return ClientBuilder.newBuilder().register(JacksonJsonProvider.class).build();
    }
}
