pipeline {
    agent any

    options {
        // Keep 4 builds maximum
        buildDiscarder(logRotator(numToKeepStr: '4'))
    }

    parameters {
      string(name: 'pactConsumerTags', defaultValue: 'master')
    }

    stages {
        stage('Build Project') {
              steps {
                sh """cd consumer && ./mvnw clean -e install"""
                }
        }

        stage('Publish Pacts') {
          steps {
            sh """cd consumer && ./mvnw pact:publish -Dpact.consumer.version=${GIT_COMMIT} -Dpact.tag=${BRANCH_NAME} -Dpact.broker.url=http://broker_app:80"""
            }
        }

        stage ('Verify Pacts') {
          steps {
            sh """cd producer && ./mvnw clean verify -Dpact.provider.version=${GIT_COMMIT} -Dpact.verifier.publishResults=true -Dpactbroker.tags=${params.pactConsumerTags}"""
          }
        }

        stage('Check Pact Verifications') {
          steps {
            sh """cd ~ && ./pact/bin/pact-broker can-i-deploy --retry-while-unknown=12 --retry-interval=10 -a consumer -b http://broker_app:80 -e ${GIT_COMMIT}"""
          }
        }
    }

     post {
        always {
          deleteDir()
        }
      }
}