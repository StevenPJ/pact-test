package com.example.pact.producer

import au.com.dius.pact.provider.junit.PactRunner
import au.com.dius.pact.provider.junit.Provider
import au.com.dius.pact.provider.junit.State
import au.com.dius.pact.provider.junit.loader.PactBroker
import au.com.dius.pact.provider.junit.target.HttpTarget
import au.com.dius.pact.provider.junit.target.Target
import au.com.dius.pact.provider.junit.target.TestTarget
import org.junit.BeforeClass
import org.junit.runner.RunWith
import org.springframework.boot.SpringApplication

@RunWith(PactRunner.class)
@Provider("Alice Service")
@PactBroker(host = "localhost", port = "80")
class ContractTest {

    @TestTarget
    public final Target target = new HttpTarget("http", "localhost", 8080, "/mallory");

    @BeforeClass
    public static void start() {
        SpringApplication.run(ProducerApplication.class);
    }

    @State("there is some good mallory")
    void toGetState() { }

}