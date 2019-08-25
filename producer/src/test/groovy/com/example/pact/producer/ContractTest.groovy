package com.example.pact.producer

import au.com.dius.pact.provider.junit.Provider
import au.com.dius.pact.provider.junit.State
import au.com.dius.pact.provider.junit.loader.PactBroker
import au.com.dius.pact.provider.junit.target.Target
import au.com.dius.pact.provider.junit.target.TestTarget
import au.com.dius.pact.provider.spring.SpringRestPactRunner
import au.com.dius.pact.provider.spring.target.SpringBootHttpTarget
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

import java.util.stream.Collectors
import java.util.stream.IntStream

import static org.mockito.ArgumentMatchers.*
import static org.mockito.Mockito.when

@RunWith(SpringRestPactRunner.class)
@Provider("provider")
@PactBroker(host = "broker_app", port = "80", tags = "master")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContractTest {

    @MockBean
    UserStore store

    @TestTarget
    public final Target target = new SpringBootHttpTarget()

    @State("a user exists")
    def "should return user"() {
        when(store.find(anyLong())).thenReturn(new User("Steve", 27))
    }

    @State("a user can be created")
    def "should create user"() {
        when(store.create(any(User.class))).thenAnswer({ i -> i.getArguments()[0] })
    }

    @State("5 users exists")
    def "should return 5 users"() {
        when(store.findUpTo(anyInt())).thenAnswer({ i ->
            IntStream.rangeClosed(1, i.getArguments()[0])
                    .mapToObj({ o -> new User("Steve", 27) })
                    .collect(Collectors.toList())
        })
    }
}