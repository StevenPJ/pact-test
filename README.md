### Contract Testing With Pact Example
This project explores the usage of contract testing between consumer and producer services using a Pact Broker

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

There are different branches which simulate different use cases. 
* `master` simulates happy path where the contract is verified by both the consumer and producer
* `consumer-breaks-pact` explores what happens when the consumer tests violate the contract
* `producer-breaks-pact` explores what happens when the producer implementation breaks the contract
* `producer-changes-pact` explores what happens when the consumer changes the contract

For each, run the following command from the multi-maven project root
```
 mvn clean install
```

Visit localhost:80 to view the pact broker UI for test results