pipeline {
    // Defines that the pipeline can run on any available Jenkins agent
    agent any

    // Ensure Maven and JDK are configured in your Jenkins Global Tool Configuration
    // Update the names below to match your configured tool names if necessary.
     tools {
         maven 'Maven' 
         jdk 'JDK 17'
     }

    environment {
        // Connects Jenkins to the Windows Docker Desktop engine
        DOCKER_HOST = 'tcp://host.docker.internal:2375'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }
        
        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }
        
        stage('Docker Build') {
            steps {
                script {
                    sh 'docker build -t java-demo-app:latest .'
                }
            }
        }
        
        stage('Deploy') {
            steps {
                script {
                    sh '''
                        docker stop java-demo-container || true
                        docker rm java-demo-container || true
                        docker run -d --name java-demo-container java-demo-app:latest
                    '''
                }
            }
        }
    }
    
    post {
        success {
            echo "Pipeline executed successfully! The application has been built, tested, and deployed."
        }
        failure {
            echo "Pipeline failed. Please check the logs above to identify the issue."
        }
    }
}
