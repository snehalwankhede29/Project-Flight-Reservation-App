# Deploy Frontend & Backend by creating Pipeline with the help of ubuntu, aws, Docker, Terraform, Kubernetes, Jenkins, Sonarqube and git & github

Step 1: launch 3 servers with high configuration
-->  Terraform-server   --- for creating an Project-Infra
-->  Sonarqube-server   --- for installing java and sonarqube    (port-no. 9000)
-->  Jenkins-server     --- for installing Jenkins, java, Maven, Docker, Configure docker-hub, CLI, Kubectl  (port-no. 8080)
  


Step 2: Create Terraform Infra   (run.tf ----- terraform init, terraform plan, terraform apply)
-->  S3    --- for frontend
-->  EKS   --- for Backend
-->  RDS   --- for Database



Step 3: Get access of Sonarqube-server (refer sonar-readme.md file for sonarqube installation)
--> get access of server      --- public-ip:9000 
--> generate-token            --- copy it and paste it somewhere                  sqp_1b321abc1f8a7cc48793fdae12b50dc75eba3da4
--> maven                     --- copy it and paste it somewhere

mvn clean verify sonar:sonar \
  -Dsonar.projectKey=sonarqube-project \
  -Dsonar.projectName='sonarqube-project' \
  -Dsonar.host.url=http://18.204.222.184:9000 \
  -Dsonar.token=sqp_1b321abc1f8a7cc48793fdae12b50dc75eba3da4




Step 4:  Get access of Jenkins-server      (public-ip:8080)
--> Install Plugins  --- Git, Sonarqube-scanner, Pipeline, pipeline-stage-view
--> Integrate Sonarqube in jenkins with token    ---setting/system/Add--SonarQube --secret text
--> Restart Jenkins-server

1 > create pipeline for backend:
                     1. stage git-pull           ---- git repo            pipeline-syntax = (git)
                     2. stage build              ---- with maven                            (sh)
                     3. stage test               ---- sonarqube                             (withSonarQubeEnv: Prepare SonarQube Scanner environment)
                     4. stage image              ---- create docker-image 
                     5. deploy                   ---- kubernetes

2 > create pipeline for fronend:
                     1. stage git-pull           ---- git repo
                     2. stage build              ---- with maven
                     3. stage move               ---- s3
                    


# Some steps needs to add in pipeline
1)  stage-2-build {
    ''' cd <file-name-where-pom.xml is present> 
        mvn clean package -DskipTests ''' 
}

2)  stage-3-test {
    (installationName = 'sonar', Cred......)
    {sh '''
    cd <file-name-where-pom.xml is present>
    mvn sonar:sonar -Dsonar.projectKey=sonarqube-project 
    '''}
}


--- Install Everything required

3)  Install --- apt install docker.io
    -- docker build -t snehalwankhede291/backend-image:latest
    -- docker push snehalwankhede291/backend-image:latest
    -- docker rmi snehalwankhede291/backend-image:latest


4)  add Jenkins-user in docker-group
   --> gpasswd -a jenkins docker  
   --> su - jenkins
   --> exit
   --> docker login, ls, ls -a
   --> cp -rvf .docker/ /var/lib/jenkins
   --> chown jenkins -R /var/lib/jenkins/.docker/

5)  Install CLI
    --> aws eks update-kubeconfig --name <cluster-name> --region <region-name>


6)  last-check
   --> kubectl get pods
   --> kubectl get services
   --> kubectl get nodes

   --> ls -a
   --> cp -rvf .kube/ /var/lib/jenkins/
   --> chown jenkins -R /var/lib/jenkins/.kube/
   --> cp -rvf .aws/ /var/lib/jenkins/
   --> chown jenkins -R /var/lib/jenkins/.aws/
   --> systemctl restart jenkins