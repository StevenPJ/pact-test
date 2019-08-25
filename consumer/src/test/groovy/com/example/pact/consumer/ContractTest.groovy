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
        properties = 'user-service.base-url:http://localhost:${RANDOM_PORT}')
class ContractTest extends Specification {

    public static RandomPortRule randomPort

    def setupSpec() {
        randomPort = new RandomPortRule()
        randomPort.before()
    }

    @Rule
    PactProviderRuleMk2 provider = new PactProviderRuleMk2("provider", null, randomPort.getPort(), this)

    @Autowired
    ApiClient client

    @Pact(consumer = "consumer")
    RequestResponsePact getUser(PactDslWithProvider builder) {
        return builder
            .given('a user exists')
                .uponReceiving('a retrieve User request')
                .path("/user")
                .method("GET")
            .willRespondWith()
                .status(200)
                .headers(['Content-Type': 'application/json'])
                .body(newJsonBody({ o -> o
                    .stringType('name', 'User')
                    .numberType('age', 2)
                }).build())
            .toPact()
    }

    @Pact(consumer = "consumer")
    RequestResponsePact createUser(PactDslWithProvider builder) {
        return builder
            .given('a user can be created')
                .uponReceiving('a create User request')
                .path("/user")
                .method("POST")
                .body(newJsonBody({ o -> o
                        .stringValue('name', 'User')
                        .numberValue('age', 2)
                }).build())
            .willRespondWith()
                .status(200)
                .headers(['Content-Type': 'application/json'])
                .body(newJsonBody({ o -> o
                        .stringValue('name', 'User')
                        .numberValue('age', 2)
                }).build())
            .toPact()
    }

    @Pact(consumer = "consumer")
    RequestResponsePact queryUsers(PactDslWithProvider builder) {
        return builder
            .given('5 users exists')
                .uponReceiving('a query Users request')
                .path("/users")
                .query("limit=5")
                .method("GET")
            .willRespondWith()
                .status(200)
                .headers(['Content-Type': 'application/json'])
                .body(newJsonBody({ o -> o
                        .object('query',  { q -> q.numberType('limit', 5) })
                        .eachLike('users', { m -> m.stringType('name', 'User').numberType('age', 2) })
                }).build())
            .toPact()
    }

    @PactVerification(fragment = "getUser")
    def "should fetch a user"() {
        given:
        def user = client.getUser()
        expect:
        user.name == 'User'
        user.age == 2
    }

    @PactVerification(fragment = "createUser")
    def "should create a user"() {
        given:
        def user = client.updateUser(new ApiClient.User("User", 2))
        expect:
        user.name == 'User'
        user.age == 2
    }

    @PactVerification(fragment = "queryUsers")
    def "should query users"() {
        given:
        def usersQuery = client.fetchUsers(5)
        expect:
        usersQuery.users.first().name == 'User'
        usersQuery.users.first().age == 2
    }
}