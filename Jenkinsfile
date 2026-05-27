pipeline {

    agent any

    stages {

        stage('Run Tests') {
            steps {
                sh 'mvn clean test'
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