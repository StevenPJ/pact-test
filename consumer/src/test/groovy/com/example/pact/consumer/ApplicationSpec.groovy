package com.example.pact.consumer


import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class ApplicationSpec extends Specification {

    def "when context is loaded then all expected beans are created"() {
        expect: "the WebController is created"
    }

}