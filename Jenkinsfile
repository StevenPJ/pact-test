pipeline {
    agent any

    stages {
        stage('Build Project') {
              steps {
                sh """cd consumer && ./mvnw clean -e install"""
                }
        }

        stage('Publish Pacts') {
          steps {
            sh """cd consumer &&  ./mvnw pact:publish -Dpact.consumer.version=${GIT_COMMIT} -Dpact.tag=${BRANCH_NAME} -Dpact.broker.url=localhost:80"""
            }
        }
    }
}K