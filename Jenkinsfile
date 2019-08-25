pipeline {
    agent any

    stages {
        stage('Build Project') {
              steps {
                sh """cd consumer && ./mvnw clean -e install"""
                }
        }


    }
}