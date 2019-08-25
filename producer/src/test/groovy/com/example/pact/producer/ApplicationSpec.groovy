package com.example.pact.producer


import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationSpec extends Specification {

    def "when context is loaded then all expected beans are created"() {
        expect: "the WebController is created"
    }

}