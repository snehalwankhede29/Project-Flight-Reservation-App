pipeline {
    agent any 
    stages {
        stage("pull"){
            steps {
                git branch: 'main', url: 'https://github.com/snehalwankhede29/Project-Flight-Reservation-App.git'
            }
        }
        stage("build"){
            steps {
                sh '''cd Backend
                    mvn clean package -DskipTests'''
            }
        }
        stage("test"){
            steps {
                withSonarQubeEnv(installationName: 'sonar', credentialsId: '20a1fc94-ce97-419f-bd5d-163f66235c1a') {
                sh'''
                    cd Backend
                    mvn sonar:sonar -Dsonar.projectKey=sonarqube-project
                    '''   
                }
            }
        }
        stage("image"){
            steps {
                sh'''
                cd Backend
                docker build -t snehalwankhede291/backend-image:latest
                docker push snehalwankhede291/backend-image:latest
                docker rmi snehalwankhede291/backend-image:latest
                '''
            }
        }
        stage("deploy"){
            steps {
                
            }
        }
    }
}