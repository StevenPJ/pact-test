pipeline {
    agent any

     options {
        // Keep 4 builds maximum
        buildDiscarder(logRotator(numToKeepStr: '4'))
      }

    stages {
        stage('Build Project') {
              steps {
                sh """cd consumer && ./mvnw clean -e install"""
                }
        }

        stage('Publish Pacts') {
          steps {
            sh """cd consumer &&  ./mvnw pact:publish -Dpact.consumer.version=${GIT_COMMIT} -Dpact.tag=${BRANCH_NAME} -Dpact.broker.url=http://broker_app:80"""
            }
        }

        stage ('Verify Pacts') {
          steps {
            sh 'cd producer ./mvnw clean verify
              -Dpact.provider.version=${GIT_COMMIT}
              -Dpact.verifier.publishResults=true'
          }
        }
    }

     post {
        always {
          deleteDir()
        }
      }
}