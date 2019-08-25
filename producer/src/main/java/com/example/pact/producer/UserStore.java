package com.example.pact.producer;

import java.util.List;

public interface UserStore {

    User find(long l);
    User create(User user);
    List<User> findUpTo(int limit);
}
