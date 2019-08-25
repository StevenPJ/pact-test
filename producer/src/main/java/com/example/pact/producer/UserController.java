package com.example.pact.producer;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@AllArgsConstructor
public class UserController {

    private UserStore store;

    @GetMapping(value = "user", produces = "application/json")
    public @ResponseBody User getUser() {
        return store.find(1L);
    }

    @PostMapping(value = "user", produces = "application/json")
    public @ResponseBody User postUser(@RequestBody User user) {
        return store.create(user);
    }

    @GetMapping(value = "users", produces = "application/json")
    public @ResponseBody
    UserQuery fetchMallories(@RequestParam("limit") int limit) {
        List<User> users = store.findUpTo(limit);
        return new UserQuery(new Query(limit), users);
    }
}
