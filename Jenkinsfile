pipeline {
    // Defines that the pipeline can run on any available Jenkins agent
    agent any

    // Ensure Maven and JDK are configured in your Jenkins Global Tool Configuration
    // Update the names below to match your configured tool names if necessary.
    tools {
        // e.g., 'Maven 3.x' or 'M3' depending on your Jenkins setup
        maven 'Maven' 
        jdk 'JDK 17'
    }

    stages {
        stage('Checkout') {
            steps {
                // Checks out the code from the Git repository
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                // Compiles the Java code
                sh 'mvn clean compile'
            }
        }
        
        stage('Test') {
            steps {
                // Runs JUnit tests
                sh 'mvn test'
            }
            post {
                always {
                    // Publishes JUnit test results to the Jenkins UI
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Package') {
            steps {
                // Packages the compiled code into a JAR file, skipping tests since they ran in the previous stage
                sh 'mvn package -DskipTests'
            }
        }
        
        stage('Docker Build') {
            steps {
                // Builds a Docker image using the provided Dockerfile
                script {
                    sh 'docker build -t java-demo-app:latest .'
                }
            }
        }
        
        stage('Deploy') {
            steps {
                // Simulates deployment by running the Docker container locally
                // In a real environment, this might be deploying to Kubernetes or an external server
                script {
                    sh '''
                        # Stop and remove the old container if it exists
                        docker stop java-demo-container || true
                        docker rm java-demo-container || true
                        
                        # Run the new container in the background
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
