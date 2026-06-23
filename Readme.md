# 🚀 Flight Reservation App – End-to-End CI/CD Deployment

## 📌 Project Overview

This project demonstrates an end-to-end DevOps implementation for deploying a Flight Reservation application using modern CI/CD practices and cloud infrastructure.

The deployment pipeline automates infrastructure provisioning, code quality analysis, containerization, and Kubernetes deployment using the following technologies:

* Ubuntu Servers
* AWS Cloud
* Terraform (Infrastructure as Code)
* Docker
* Kubernetes (Amazon EKS)
* Jenkins
* SonarQube
* Git & GitHub
* Maven
* MySQL / Amazon RDS
* Amazon S3

---

# 🏗️ Architecture

```
                GitHub Repository
                        │
                        ▼
                  Jenkins Pipeline
        ┌───────────────┼────────────────┐
        │               │                │
        ▼               ▼                ▼
   Build (Maven)   SonarQube Scan   Docker Build & Push
                                            │
                                            ▼
                                      Docker Hub
                                            │
                                            ▼
                                   Kubernetes (EKS)
                                            │
                                            ▼
                                 Flight Backend Service

Frontend
GitHub → Jenkins → Build → Amazon S3

Database
Amazon RDS (MySQL)
```

---

# 🖥️ Step 1: Launch EC2 Servers

Create three Ubuntu EC2 instances with sufficient resources.

| Server           | Purpose                   | Port |
| ---------------- | ------------------------- | ---- |
| Terraform Server | Create AWS Infrastructure | -    |
| SonarQube Server | Code Quality Analysis     | 9000 |
| Jenkins Server   | CI/CD Automation          | 8080 |

---

# ☁️ Step 2: Create AWS Infrastructure Using Terraform

Initialize Terraform:

```bash
terraform init
```

Preview execution plan:

```bash
terraform plan
```

Apply infrastructure:

```bash
terraform apply
```

Terraform provisions:

* Amazon S3 Bucket (Frontend Hosting)
* Amazon EKS Cluster (Backend Deployment)
* Amazon RDS MySQL Instance (Database)

---

# 📥 Step 3: Clone the Repository

```bash
git clone https://github.com/shubhamkalsait/Flight-reservation.git

cd Flight-reservation
```

---

# 🛢️ Step 4: Database Setup

## Option A: Local MySQL

```bash
apt update -y
apt install mysql-server -y

mysql_secure_installation

mysql -uroot -p
```

Inside MySQL:

```sql
CREATE USER linux IDENTIFIED BY 'Redhat';
GRANT ALL PRIVILEGES ON *.* TO linux;
FLUSH PRIVILEGES;
CREATE DATABASE flightdb;
EXIT;
```

## Option B: Amazon RDS

Install MySQL client:

```bash
apt install mysql-client-core-8.0 -y
```

Connect to RDS:

```bash
mysql -h <rds-endpoint> -P 3306 -u admin -p
```

Create database:

```sql
CREATE DATABASE Flightdb;
SHOW DATABASES;
```

---

# 🔍 Step 5: Install and Configure SonarQube

## Install Java

```bash
apt install openjdk-17-jdk -y
```

## Install PostgreSQL

```bash
apt install postgresql -y

systemctl start postgresql
```

Configure PostgreSQL:

```bash
sudo -u postgres psql
```

```sql
CREATE USER linux PASSWORD 'redhat';
CREATE DATABASE sonarqube;
GRANT ALL PRIVILEGES ON DATABASE sonarqube TO linux;
\c sonarqube
GRANT ALL PRIVILEGES ON SCHEMA public TO linux;
\q
```

## Configure Linux Kernel

```bash
sysctl -w vm.max_map_count=524288
sysctl -w fs.file-max=131072

ulimit -n 131072
ulimit -u 8192
```

## Install SonarQube

```bash
wget https://binaries.sonarsource.com/Distribution/sonarqube/sonarqube-25.5.0.107428.zip

apt install unzip -y

unzip sonarqube-25.5.0.107428.zip

mv sonarqube-25.5.0.107428 /opt/sonar
```

Edit configuration:

```bash
vim /opt/sonar/conf/sonar.properties
```

Add:

```text
sonar.jdbc.username=linux
sonar.jdbc.password=redhat
sonar.jdbc.url=jdbc:postgresql://localhost/sonarqube
```

Create Sonar user:

```bash
useradd sonar -m

chown -R sonar:sonar /opt/sonar
```

Start SonarQube:

```bash
su sonar

cd /opt/sonar/bin/linux-x86-64

./sonar.sh start

./sonar.sh status
```

Access:

```
http://<public-ip>:9000
```

Generate a SonarQube token:

```
Profile → Security → Generate Token
```

---

# ⚙️ Step 6: Install Jenkins

Install Jenkins on the Jenkins server (using the official installation method).

Install Maven:

```bash
apt install maven -y
```

Access:

```
http://<public-ip>:8080
```

Install plugins:

* Git Plugin
* Pipeline
* Pipeline Stage View
* SonarQube Scanner

Configure SonarQube server in:

```
Manage Jenkins
→ Configure System
→ SonarQube Servers
```

Add the generated Sonar token as Secret Text credentials.

Restart Jenkins.

---

# 🐳 Step 7: Install Docker on Jenkins Server

```bash
apt install docker.io -y
```

Add Jenkins user to Docker group:

```bash
gpasswd -a jenkins docker
```

or

```bash
usermod -aG docker jenkins
```

Refresh session:

```bash
su - jenkins
exit
```

Copy Docker authentication:

```bash
docker login

cp -rvf ~/.docker /var/lib/jenkins/

chown -R jenkins /var/lib/jenkins/.docker
```

---

# ☸️ Step 8: Install kubectl and AWS CLI

## Install kubectl

```bash
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"

install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl

kubectl version --client
```

## Install AWS CLI

```bash
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o awscliv2.zip

apt install unzip -y

unzip awscliv2.zip

./aws/install
```

Configure credentials:

```bash
aws configure
```

Provide:

* Access Key
* Secret Key
* Default Region
* Output Format (json)

---

# 🔗 Step 9: Connect Jenkins to Amazon EKS

Update kubeconfig:

```bash
aws eks update-kubeconfig \
  --name <cluster-name> \
  --region <region-name>
```

Copy Kubernetes configuration:

```bash
cp -rvf ~/.kube /var/lib/jenkins/

chown -R jenkins /var/lib/jenkins/.kube

cp -rvf ~/.aws /var/lib/jenkins/

chown -R jenkins /var/lib/jenkins/.aws

systemctl restart jenkins
```

Verify:

```bash
kubectl get nodes
kubectl get pods
kubectl get services
```

---

# 🔨 Step 10: Backend Build

Install dependencies:

```bash
apt install openjdk-17-jdk -y
apt install maven -y
```

Set environment variables:

```bash
export DATASOURCE_URL="jdbc:mysql://localhost:3306/flightdb"

export DATASOURCE_USER="linux"

export DATASOURCE_PASSWORD="Redhat"

export FRONTEND_URL="http://localhost:80"
```

Build:

```bash
cd FlightReservationSystem

mvn clean package
```

Run locally:

```bash
java -jar target/flight*.jar
```

---

# 🌐 Step 11: Frontend Build

```bash
cd frontend

apt install nodejs npm -y

export VITE_API_URL=http://localhost:8080

npm install

npm run build
```

Deploy to Apache:

```bash
apt install apache2 -y

cp dist/* /var/www/html/

systemctl start apache2
```

For production, upload the build artifacts to the configured Amazon S3 bucket.

---

# 🔎 Step 12: Run SonarQube Analysis

```bash
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=sonarqube-project \
  -Dsonar.projectName=sonarqube-project \
  -Dsonar.host.url=http://<public-ip>:9000 \
  -Dsonar.token=<generated-token>
```

---

# 🐋 Step 13: Build and Push Docker Image

```bash
docker build -t <dockerhub-user>/<image-name>:latest .

docker push <dockerhub-user>/<image-name>:latest

docker rmi <dockerhub-user>/<image-name>:latest
```

---

# 🚀 Step 14: Jenkins CI/CD Pipeline

Pipeline stages:

1. Git Pull
2. Maven Build
3. SonarQube Analysis
4. Docker Build & Push
5. Deploy to Amazon EKS

Example Build stage:

```groovy
stage('Build') {
    steps {
        sh '''
        cd FlightReservationSystem
        mvn clean package -DskipTests
        '''
    }
}
```

Example SonarQube stage:

```groovy
stage('SonarQube Analysis') {
    steps {
        withSonarQubeEnv('sonar') {
            sh '''
            cd FlightReservationSystem
            mvn sonar:sonar \
            -Dsonar.projectKey=sonarqube-project
            '''
        }
    }
}
```

Example Docker stage:

```groovy
stage('Docker Image') {
    steps {
        sh '''
        docker build -t <dockerhub-user>/<image-name>:latest .

        docker push <dockerhub-user>/<image-name>:latest

        docker rmi <dockerhub-user>/<image-name>:latest
        '''
    }
}
```

Example Deployment stage:

```groovy
stage('Deploy') {
    steps {
        sh '''
        kubectl apply -f k8s/
        '''
    }
}
```

---

# 📝 Step 15: Update Application Configuration

Before deployment:

* Update `application.properties`

  * Database endpoint
  * Username
  * Password

* Update Kubernetes deployment manifest:

  * Docker image name
  * Image tag

Commit and push changes to GitHub so Jenkins can execute the pipeline automatically.

---

# ✅ Final Verification

Verify the following after deployment:

* Terraform infrastructure created successfully
* SonarQube accessible on port **9000**
* Jenkins accessible on port **8080**
* Docker image pushed to Docker Hub
* Backend deployed successfully on Amazon EKS
* Frontend hosted on Amazon S3
* Amazon RDS connected successfully
* Kubernetes pods running
* Kubernetes services exposed correctly

Useful commands:

```bash
kubectl get nodes

kubectl get pods

kubectl get services
```

---

# 🎉 Outcome

After completing all the above steps:

* Infrastructure is provisioned using Terraform.
* Code is version-controlled with Git and GitHub.
* Jenkins automatically builds and deploys the application.
* SonarQube performs static code quality analysis.
* Docker packages the application into containers.
* Images are pushed to Docker Hub.
* Kubernetes (Amazon EKS) deploys the backend.
* Amazon S3 serves the frontend.
* Amazon RDS stores application data.

This setup provides a complete end-to-end CI/CD pipeline for the Flight Reservation application using modern DevOps practices.
