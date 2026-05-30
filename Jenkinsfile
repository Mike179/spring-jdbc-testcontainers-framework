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

        stage('Docker Check') {
            steps {
                sh 'docker --version'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t banking-tests .'
            }
        }

        stage('Run Tests In Docker') {
            steps {
                sh '''
                mkdir -p target/allure-results

                docker run --rm \
                  -e DB_URL="$DB_URL" \
                  -e DB_USER="$DB_USER" \
                  -e DB_PASSWORD="$DB_PASSWORD" \
                  -v $WORKSPACE/target/allure-results:/app/target/allure-results \
                  banking-tests
                '''
            }
        }

        stage('Generate Allure Report') {
            steps {
                allure([
                    includeProperties: false,
                    jdk: '',
                    results: [[path: 'target/allure-results']]
                ])
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