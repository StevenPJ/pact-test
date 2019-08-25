package com.example.pact.consumer;

import lombok.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "user-client", url = "${user-service.base-url}")
public interface ApiClient {

    @GetMapping(value = "/user")
    User getUser();

    @PostMapping(value = "/user")
    User updateUser(@RequestBody User user);

    @GetMapping(value = "/users")
    UserQuery fetchUsers(@RequestParam("limit") int limit);


    @Value
    class User {
        private String name;
        private int age;
    }

    @Value
    class UserQuery {
        private Object query;
        private List<User> users;
    }
}
