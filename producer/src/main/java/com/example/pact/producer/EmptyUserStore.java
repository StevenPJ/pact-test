package com.example.pact.producer;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmptyUserStore implements UserStore {
    @Override
    public User find(long l) {
        return null;
    }

    @Override
    public User create(User user) {
        return null;
    }

    @Override
    public List<User> findUpTo(int limit) {
        return null;
    }
}
