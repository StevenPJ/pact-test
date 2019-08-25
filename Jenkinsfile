pipeline {
    agent any

    stages {

        stage('Enter Consumer dir') {
              steps {
                sh """cd consumer"""
                }
        }

        stage('Build Project') {
              steps {
                sh """./mvnw clean install"""
                }
        }

        stage('Publish Pacts') {
          steps {
            sh """./mvnw pact:publish -Dpact.consumer.version=${GIT_COMMIT} -Dpact.tag=${BRANCH_NAME} -Dpact.broker.url=localhost:80"""
            }
        }
    }
}