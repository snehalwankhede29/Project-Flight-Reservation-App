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
                sh '''cd frontend
                    npm install
                    npm run build'''
            }
        }
        stage('Deploy'){
            steps{
                sh '''
                    cd frontend
                    aws s3 sync dist/ s3://my-first-bucket-for-frontend123123/
                '''    
            }
        } 
    }
}    