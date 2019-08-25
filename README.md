### Contract Testing With Pact Example
This project explores the usage of contract testing between consumer and producer services using a Pact Broker

This example follows some guidance from [here](https://kreuzwerker.de/blog/writing-contract-tests-with-pact-in-spring-boot)

## Setup
Start the pact broker locally using the docker-compose file location in the broker director
```
 cd broker/
 docker-compose up -d
```

Build and install the project to start the following processes
* The Consumer declares the contract and publishes to the pact broker
* The Producer verifies itself against the contract
* Results are published back to the broker

Run the following command from the multi-maven project root to run the contract tests
```
 mvn clean install
```

Visit localhost:80 to view the pact broker UI for test results


### Notes

Consumers define the contract by declaring a pact. An example of this is below

```java
@Pact(consumer = "consumer") // Pact declaration, defining the name of the consumer
RequestResponsePact getUser(PactDslWithProvider builder) { 
    return builder 
        .given('a user exists') // the state of the provider
            .uponReceiving('a retrieve User request') // description
            .path("/user") // provider endpoint
            .method("GET")
        .willRespondWith()
            .status(200)
            .headers(['Content-Type': 'application/json'])
            .body(newJsonBody({ o -> o // LambdaDSL used to declare the structure of the response
                .stringType('name', 'User') // types declared in response with example
                .numberType('age', 2) // types declared in the response with example
            }).build())
        .toPact()
}
```
The consumer can then verify the pact with the following

```java
@PactVerification(fragment = "getUser") // name of the method defining the pact
def "should fetch a user"() {
    given:
    def user = client.getUser()
    expect:
    user.name == 'User'
    user.age == 2
}
```

It is important to use types when building the contract. This decouples the contract from the specific values 
returned by the provider, and keeps the contract focused only on structure i.e. field names and types. When the examples are specified,
then the mock server created for the consumer tests will return the examples in the response, which allows consumer
verification tests to match against the specified examples. The provider however is not required to return these
specific examples during its verification tests.

A matching producer verification test looks like the below
```java
@State("a user exists") // matching state of contract
def "should return user"() {
    when(store.find(anyLong())).thenReturn(new User("Steve", 27))
}
```

Note that the provider returns different examples that in the contract. As types were defined in the contract, the provider
implementation just needs to satisfy the matchers and not the examples.

#### Test Setup

When creating consumer verification tests, it is important to use as much of the actual client code as possible 
to ensure the contract tests provide value. When using OpenFeign with Spring, the following setup can be used

```java
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "user-service.base-url:http://localhost:8080")
class ContractTest extends Specification {

    @Rule
    PactProviderRuleMk2 provider = new PactProviderRuleMk2("provider", null, 8080, this);

    @Autowired
    ApiClient client;
    
    .....
```
In this example, we do not start a Web Environment as we do not want to test incoming requests (as we are the consumer and are testing outgoing requests only).
We declare a provider rule, specifying the name of the provider and the port we want the mock server simulating the provider to run on.
We then configure out FeignClient to point to our mocked provider and autowire the real feign client for use
during our consumer verification tests. When running pacts as part of CI, it may be neccessary to use a random free port instead of a fixed port as used here.
This can be achieved by using a [JUnit rule](https://github.com/tinexw/cdc-with-pact/blob/master/messaging-app/src/test/java/de/kreuzwerker/cdc/messagingapp/UserServiceContractTest.java#L29) and Spring properties.

The following two statements:

```java
@Rule
PactProviderRuleMk2 provider = new PactProviderRuleMk2("provider", null, 8080, this);

..... 

@Pact(consumer = "consumer") 
.....
```

means that our pact will be created as `consumer-provider.json` and will declare contracts between two services name `consumer` and `provider`
When these tests are run, the pact is created to the `target/pacts` directory:

```
{
  "provider": {
    "name": "provider"
  },
  "consumer": {
    "name": "consumer"
  },
  "interactions": [
    {
      "description": "a retrieve User request",
      "request": {
        "method": "GET",
        "path": "/user"
      },
      "response": {
        "status": 200,
        "headers": {
          "Content-Type": "application/json"
        },
        "body": {
          "name": "User",
          "age": 2
        },
        "matchingRules": {
          "body": {
            "$.name": {
              "matchers": [
                {
                  "match": "type"
                }
              ],
              "combine": "AND"
            },
            "$.age": {
              "matchers": [
                {
                  "match": "number"
                }
              ],
              "combine": "AND"
            }
          }
        }
      },
      "providerStates": [
        {
          "name": "a user exists"
        }
      ]
    }
  ],
  "metadata": {
    "pactSpecification": {
      "version": "3.0.0"
    },
    "pact-jvm": {
      "version": "3.6.12"
    }
  }
}
```
This pact can be published using the `mvn pact:publish` command.
In this example, we configure maven to publish the pacts to a pact broker, using the following plugin
```
<plugins>
    <plugin>
        <groupId>au.com.dius</groupId>
        <artifactId>pact-jvm-provider-maven_2.12</artifactId>
        <version>3.6.12</version>
        <executions>
            <execution>
                <phase>install</phase> // publishes during the mvn install goal
                <goals>
                    <goal>publish</goal>
                </goals>
            </execution>
        </executions>
        <configuration>
            <pactBrokerUrl>http://localhost:80</pactBrokerUrl>
            <trimSnapshot>true</trimSnapshot>
            <tags>
                <tag>feature/feature_name</tag>
            </tags>
        </configuration>
    </plugin>
    ...
</plugins>
````

When creating producer verification tests, it is important to remember the purpose of the contract tests when setting up the tests.
Anything that can effect the output of a controller should not be mocked/stubbed. I.e. serialisers, custom mappers should be included
in the test config, while things like security and database layers can be mocked. As a result a provider verification test may look like:

```java
@RunWith(SpringRestPactRunner.class)
@Provider("provider")
@PactBroker(host = "localhost", port = "80")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContractTest {
    
    @MockBean
    UserStore store

    @TestTarget
    public final Target target = new SpringBootHttpTarget()

    @State("a user exists") // matches the given() from the pact creation
    def "should return user"() {
        when(store.find(anyLong())).thenReturn(new User("Steve", 27))
    }
    ...
}
```
Here we start the application on a random port, and user the SpringRestPactRunner to pull the pacts from the PactBroker, and
fire the stored requests at the application. We replace the data layer with a stub, which allows us to bring the application
into the specified state during each contract test. This contract asserts that a user should exist, so we stub our database to return
a user when requested. The stored request from the contract (setup in the consumer) is then fired and the response from the provider
is then compared to the response declared in the contract. If they match, the test goes green otherwise the test fails with
detail as to what does not match.

When using query arguments, a useful technique to keep tests focused on the contract only is to stipulate that the provider should echo back any query arguments
it receives and uses. By doing this, the functional usage of the query arguments is not tested, and the verification will fail if the provider and consumer diverge
on the query arguments that should be used.

### Scenarios
Here we think about different scenarios and how we want PACT to behave

##### Provider adds a field to the API
Everything still passes

##### Provider removes a field from the API
Provider verification will fail as consumer contract is violated

##### Provider changes a fields format
Provider verification will fail as consumer contract is violated

##### Consumer changes API implementation
Consumer verification tests will fail if a field is changed.

##### Consumer changes the contract
Consumer verification will pass if the verifications are also updated. However the provider will fail verification until if 
changes to satisfy the contract. Generic provider states that accept arguments allow more flexible verifications to using 
concrete provider states, as when the concrete provider state is not declared in the provider, then the test will automatically fail
without useful output.

### To Do
Look at CI and webhooks with the pact broker and techniques to satisfy new scenarios to ensure
* No down time
* Fixes are not blocked from entering production

Then automate the CI/webhooks creation
Also look into creating a library to assist in creation of pacts when using openfeign/springMVC
