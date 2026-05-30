pipeline {

    agent any

    tools {
        maven 'Maven'
    }

    environment {
        DB_URL = credentials('db-url')
        DB_USER = credentials('db-user')
        DB_PASSWORD = credentials('db-password')
    }

    stages {

        stage('Environment Info') {
            steps {
                sh 'java -version'
                sh 'mvn -version'
            }
        }

        stage('Run Tests') {
            steps {
                sh 'mvn clean test'
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished'
        }

        success {
            echo 'Tests passed'
        }

        failure {
            echo 'Tests failed'
        }
    }
}