package com.example.pact.consumer;

import lombok.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "mallory-client", url = "${mallory-service.base-url}")
public interface ApiClient {

    @GetMapping(value = "/mallory")
    Mallory getMallory();

    @Value
    class Mallory {
        private String name;
        private int age;
    }
}
