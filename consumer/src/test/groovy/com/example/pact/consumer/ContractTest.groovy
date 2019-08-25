package com.example.pact.consumer

import au.com.dius.pact.consumer.Pact
import au.com.dius.pact.consumer.PactProviderRuleMk2
import au.com.dius.pact.consumer.PactVerification
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.model.RequestResponsePact
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "mallory-service.base-url:http://localhost:8080")
class ContractTest extends Specification {

    @Rule
    PactProviderRuleMk2 provider = new PactProviderRuleMk2("provider", null, 8080, this);

    @Autowired
    ApiClient client;

    @Pact(consumer = "consumer")
    RequestResponsePact malloryExists(PactDslWithProvider builder) {
        return builder
            .given('there is some good mallory')
                .uponReceiving('a retrieve Mallory request')
                .path("/mallory")
                .method("GET")
            .willRespondWith()
                .status(200)
                .headers(['Content-Type': 'application/json'])
                .body(newJsonBody({ o -> o
                    .stringType('name', 'Mallory')
                    .numberType('age', 2)
                })
            .build())
            .toPact()
    }

    @PactVerification(fragment = "malloryExists")
    def "mallory exists"() {
        given:
        def mallory = client.getMallory()
        expect:
        mallory.name == 'Mallory'
        mallory.age == 2
    }
}