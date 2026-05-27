pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/Mike179/spring-jdbc-testcontainers-framework.git'
            }
        }

        stage('Run Tests') {
            steps {
                sh 'mvn clean test'
            }
        }

        stage('Generate Allure Report') {
            steps {
                allure includeProperties: false,
                        jdk: '',
                        results: [[path: 'target/allure-results']]
            }
        }
    }

    post {
        success {
            echo 'Tests passed successfully'
        }

        failure {
            echo 'Tests failed'
        }
    }
}