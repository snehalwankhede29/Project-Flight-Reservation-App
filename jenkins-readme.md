# Install Jenkins From Google

# Install maven 
```shell
apt install maven -y
```

## Install and configure kubectl  
```shell

# Install kubectl
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
kubectl version --client

# Install AWS ClI
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
apt install unzip -y
unzip awscliv2.zip
sudo ./aws/install
aws configure                 ---- Get keys from IAM-Role
    Default region name :
    Default output format : json


# EKS-Cluster access in jenkins server
--> aws eks update-kubeconfig --name <cluster-name> --region <region-name>