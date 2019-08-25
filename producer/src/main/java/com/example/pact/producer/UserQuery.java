package com.example.pact.producer;

import lombok.Value;

import java.util.List;

@Value
class UserQuery {
    private Query query;
    private List<User> users;
}
