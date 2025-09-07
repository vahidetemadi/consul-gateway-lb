package com.example.consul_demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestRestApi {

    @Value("${spring.cloud.consul.discovery.instance-id}")
    private String instanceId;

    @GetMapping
    public String hello() {
        return "Hello world! from " + instanceId;
    }
}
