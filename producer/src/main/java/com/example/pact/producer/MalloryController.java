package com.example.pact.producer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController(value = "malllory")
public class MalloryController {

    @GetMapping(produces = "text/html")
    public @ResponseBody String getMallory() {
        return "That is some good Mallory.";
    }
}
