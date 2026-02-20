pipeline {
    agent any 
    stages {
        stage("1-pull") {
            steps {
                git branch: 'main', url: 'https://github.com/snehalwankhede29/Project-Flight-Reservation-App.git'
            }
        }
        stage("2-build") {
            steps {
                sh '''cd Backend
                    mvn clean package -DskipTests'''
            }
        }
        stage("3-test") {
            steps {
                withSonarQubeEnv(installationName: 'sonar', credentialsId: 'sonarqube') {
                    sh '''cd Backend
                    mvn clean verify sonar:sonar -DskipTests \\
                    -Dsonar.projectKey=backend-pipeline \\
                    -Dsonar.projectName=\'backend-pipeline\' \\
                    '''
                }
            }
        }
         stage("4-docker-image") {
            steps {
               sh '''
                cd Backend
                docker build -t snehalwankhede291/backend-image:latest .
                docker push snehalwankhede291/backend-image:latest
                docker rmi snehalwankhede291/backend-image:latest
                 '''
            }
        }
        stage("5-deploy") {
            steps {
                sh '''cd Backend
                kubectl apply -f k8s/'''
            }
        }
        
    }
}