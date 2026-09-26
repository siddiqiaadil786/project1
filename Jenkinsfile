pipeline {
    agent any

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
        
        stage('Dependency Tracker (Security Scan)') {
            steps {
                script {
                    echo "Scanning dependencies for vulnerabilities using OWASP Dependency-Check..."
                    // We use -s settings.xml to force Maven to use the Nexus repository
                    sh 'mvn org.owasp:dependency-check-maven:check -s settings.xml'
                }
            }
        }

        stage('Build') {
            steps {
                // Now compiling code and fetching dependencies through Nexus
                sh 'mvn clean compile -s settings.xml'
            }
        }
        
        stage('Test') {
            steps {
                sh 'mvn test -s settings.xml'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('SonarQube Analysis') {
            environment {
                // PASTE YOUR GENERATED SONAR TOKEN HERE
                SONAR_TOKEN = 'YOUR_SONAR_TOKEN_HERE' 
            }
            steps {
                script {
                    echo "Sending code and coverage reports to SonarQube..."
                    sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.host.url=http://host.docker.internal:9000 -Dsonar.login=${SONAR_TOKEN} -s settings.xml'
                }
            }
        }
        
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests -s settings.xml'
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
                        docker run -d -p 8081:8081 --name java-demo-container java-demo-app:latest
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
