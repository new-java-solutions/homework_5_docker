# Docker + Compose
sudo apt update
sudo apt install -y ca-certificates curl gnupg
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/debian/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
  https://download.docker.com/linux/debian $(. /etc/os-release && echo $VERSION_CODENAME) stable" \
  | sudo tee /etc/apt/sources.list.d/docker.list >/dev/null
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Jenkins
sudo apt install -y openjdk-17-jre
curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key | sudo tee \
  /usr/share/keyrings/jenkins-keyring.asc >/dev/null
echo "deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] \
  https://pkg.jenkins.io/debian-stable binary/" | sudo tee \
  /etc/apt/sources.list.d/jenkins.list >/dev/null
sudo apt update
sudo apt install -y jenkins

# Let Jenkins use Docker
sudo usermod -aG docker jenkins
sudo systemctl enable --now docker jenkins
# re-login (or reboot) so the jenkins group change is active



# Jenkins user=root
# 1) See the active unit file path (confirms location on your box)
systemctl cat jenkins

# 2) Stop Jenkins
sudo systemctl stop jenkins

# 3) Create a systemd override to run as root (doesn't touch vendor file)
sudo systemctl edit jenkins

# PASTE:
[Service]
User=root
Group=root


# 4) Reload systemd and start
sudo systemctl daemon-reload
sudo systemctl start jenkins



# echo '{ "insecure-registries": ["35.184.191.162:5000"] }' | sudo tee /etc/docker/daemon.json
# sudo systemctl restart docker

# sudo systemctl enable --now jenkins
#

