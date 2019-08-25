pipeline {
    agent any

    stages {

        stage('Publish Pacts') {
          steps {
            sh """cd consumer && ./mvnw pact:publish
              -Dpact.consumer.version=${GIT_COMMIT}
              -Dpact.tag=${BRANCH_NAME}
              -Dpact.broker.url=broker_app"""
            }
        }
    }
}