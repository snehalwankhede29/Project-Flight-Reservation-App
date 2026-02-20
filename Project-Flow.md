🚀 Flight Reservation App – End-to-End CI/CD Deployment

This project demonstrates an end-to-end CI/CD pipeline implementation for deploying a Frontend and Backend application using:

Ubuntu Servers
AWS Cloud
Terraform (Infrastructure as Code)
Docker
Kubernetes (EKS)
Jenkins
SonarQube
Git & GitHub


🏗️ Project Architecture Overview

The application is deployed using:
S3 → Frontend Hosting
EKS → Backend Deployment
RDS → Database
SonarQube → Code Quality Analysis
Jenkins → CI/CD Automation


🖥️ Step 1: Launch Required Servers (EC2 Instances)

Three high-configuration Ubuntu servers were created:
Server Name	Purpose	Port
Terraform Server	Create AWS Infrastructure	-
SonarQube Server	Code Quality Analysis	9000
Jenkins Server	    CI/CD Automation	    8080


🌍 Step 2: Create Infrastructure Using Terraform

From the Terraform Server:
terraform init
terraform plan
terraform apply

Resources Created:
S3 Bucket     → For Frontend Deployment
EKS Cluster   → For Backend Deployment
RDS Instance  → For Database

🔍 Step 3: SonarQube Setup --- Install Sonarqube--refer

## Access SonarQube:

http://<public-ip>:9000

Login to SonarQube
Go to Profile → Security → Generate Token
Save the token securely
Run SonarQube Analysis Using Maven

mvn clean verify sonar:sonar \
  -Dsonar.projectKey=sonarqube-project \
  -Dsonar.projectName='sonarqube-project' \
  -Dsonar.host.url=http://<public-ip>:9000 \
  -Dsonar.token=<your-generated-token>


⚙️ Step 4: Jenkins Configuration

## Access Jenkins:
http://<public-ip>:8080

# Install Required Plugins:
Git Plugin
SonarQube Scanner
Pipeline
Pipeline Stage View

# Integrate SonarQube in Jenkins:

Go to → Manage Jenkins
Configure System
Add SonarQube Server
Add Secret Text (Sonar Token)
Restart Jenkins.

🔄 Create Backend CI/CD Pipeline Stages

stage-1: Git Pull
stage-2: Build (Maven)
stage-3: SonarQube Analysis
stage-4: Docker Image Creation & Push
stage-5: Deploy to Kubernetes (EKS)

# Sample Build Stage
stage('Build') {
    steps {
        sh '''
        cd <backend-folder>
        mvn clean package -DskipTests
        '''
    }
}

# Sample SonarQube Stage
stage('SonarQube Analysis') {
    steps {
        withSonarQubeEnv('sonar') {
            sh '''
            cd <backend-folder>
            mvn sonar:sonar -Dsonar.projectKey=sonarqube-project
            '''
        }
    }
}

🐳 Docker Installation & Configuration in jenkins-server

sudo apt install docker.io -y

👤 Add Jenkins User to Docker Group
sudo gpasswd -a jenkins docker
su - jenkins
exit

Copy Docker credentials:

sudo cp -rvf .docker/ /var/lib/jenkins
sudo chown jenkins -R /var/lib/jenkins/.docker/
☸️ Configure Kubernetes (EKS)

# Build and Push Image:
docker build -t <dockerhub-name>/<docker-image-name>:latest .  
docker push <dockerhub-name>/<docker-image-name>:latest
docker rmi <dockerhub-name>/<docker-image-name>:latest  

Sample Docker-image Stage
stage('Docker-image') {
    steps {
       sh'''
            cd FlightReservationApplication
            docker build -t <dockerhub-name>/<docker-image-name>:latest .  
            docker push <dockerhub-name>/<docker-image-name>:latest
            docker rmi <dockerhub-name>/<docker-image-name>:latest
          '''
        }
    }


# Install AWS CLI & kubectl. -- refer

Update kubeconfig:
aws eks update-kubeconfig --name <cluster-name> --region <region>

# Sample Deploy Stage
stage('Deploy'){
            steps{
                sh'''
                    cd FlightReservationApplication   
                    kubectl apply -f k8s/
                '''
            }
        }

# Verify Cluster:

kubectl get nodes
kubectl get pods
kubectl get services

# Copy configuration to Jenkins:

sudo cp -rvf .kube/ /var/lib/jenkins/
sudo chown jenkins -R /var/lib/jenkins/.kube/

sudo cp -rvf .aws/ /var/lib/jenkins/
sudo chown jenkins -R /var/lib/jenkins/.aws/

sudo systemctl restart jenkins


✅ Final Verification Checklist
Terraform Infrastructure Created
SonarQube Running on Port 9000
Jenkins Running on Port 8080
Docker Image Pushed to DockerHub
Backend Running on EKS
Frontend Hosted on S3
RDS Connected Successfully

