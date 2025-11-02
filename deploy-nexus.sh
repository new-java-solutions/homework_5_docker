
# NEXUS

# 1) Prereqs
sudo apt update
sudo apt install -y wget tar openjdk-17-jre-headless

# 1) Get & unpack
mkdir -p ~/nexus && cd ~/nexus
wget -O nexus.tgz https://download.sonatype.com/nexus/3/nexus-3.85.0-03-linux-x86_64.tar.gz
tar -xzf nexus.tgz
cd nexus-3.85.0-03

# 2) Run as *your* user (required)
echo "run_as_user=\"$(whoami)\"" > bin/nexus.rc

# 3) Start in foreground
./bin/nexus run

# First-time admin password:
# cat /home/jan_kasper/apps/sonatype-work/nexus3/admin.password

# 4) Open Nexus -> Create Repository -> port http 5000
# 5) Open Nexus -> Security -> Realms -> Docke Bearer Token Realsm to Active


# Build image
# docker buildx build --platform linux/amd64 -t exchange_app:1 --load .
# docker login 35.184.191.162:5000
# docker tag exchange_app:1 35.184.191.162:5000/exchange_app:1
# docker push 35.184.191.162:5000/exchange_app:1


# docker buildx build --platform linux/amd64 -t user_app:1 --load .
# docker login 35.184.191.162:5000
# docker tag user_app:1 35.184.191.162:5000/user_app:1
# docker push 35.184.191.162:5000/user_app:1

 docker buildx build --platform linux/amd64 -t gateway_app:1 --load .
 docker login 35.184.191.162:5000
 docker tag gateway_app:1 35.184.191.162:5000/gateway_app:1
 docker push 35.184.191.162:5000/gateway_app:1



