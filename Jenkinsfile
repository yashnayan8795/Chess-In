pipeline {
    agent any

    tools {
        // Ensure you have configured a Maven tool in Jenkins named 'Maven 3.x' or similar,
        // or remove this block if Maven is already in the system PATH.
        // maven 'Maven 3.x'
        // jdk 'JDK 21'
    }

    stages {
        stage('Checkout') {
            steps {
                // Jenkins automatically checks out the code from the SCM configured in the job.
                // We echo the step for visibility.
                echo 'Checking out source code...'
                checkout scm
            }
        }

        stage('Build Project') {
            steps {
                echo 'Building Maven multi-module project...'
                // Using 'bat' because Jenkins will be running on Windows
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Run Tests') {
            steps {
                echo 'Executing unit and integration tests...'
                bat 'mvn test'
            }
        }

        stage('Docker Build') {
            steps {
                echo 'Building Docker images for all microservices...'
                bat 'docker-compose build'
            }
        }

        stage('Deploy/Run Containers') {
            steps {
                echo 'Starting the Docker containers in the background...'
                bat 'docker-compose up -d'
            }
        }
    }

    post {
        always {
            echo 'Pipeline execution complete.'
            // Optional: Publish test results
            // junit '**/target/surefire-reports/*.xml'
        }
        success {
            echo 'All stages completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Please check the logs.'
            // Optional: Stop the containers if they were started but tests/build failed
            // bat 'docker-compose down'
        }
    }
}
