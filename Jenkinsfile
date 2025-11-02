pipeline {
  agent any
  environment {
    REGISTRY = "35.184.191.162:5000"       // <— your Nexus Docker (hosted) port
    CRED_ID  = "nexus-docker"              // Jenkins credentials ID
    TAG      = "build-${env.BUILD_NUMBER}" // or use GIT_COMMIT
    SERVICES = "exchange_service,user_service,gateway_service"
  }
  triggers { pollSCM('') } // remove if using webhooks; this is a no-op poll
  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Docker') {
      steps {
        sh '''
        docker info | grep -A2 'Insecure Registries'
          mkdir -p ~/.config/docker
          tee ~/.config/docker/daemon.json >/dev/null <<'EOF'
            {
              "insecure-registries": ["35.184.191.162:5000"]
            }
          EOFū
          systemctl --user restart docker
          docker info | sed -n '/Insecure Registries:/,/^$/p'
        '''
      }
    }


    stage('Build & Push images') {
      steps {
        script {
          def svcs = SERVICES.split(',')
          sh "echo 'admin' | docker login $REGISTRY -u admin --password-stdin"
          for (s in svcs) {
            dir(s) {
              sh """
                docker build -t ${REGISTRY}/${s}:${TAG} .
                docker push ${REGISTRY}/${s}:${TAG}
                # optional latest tag
                docker tag ${REGISTRY}/${s}:${TAG} ${REGISTRY}/${s}:latest
                docker push ${REGISTRY}/${s}:latest
              """
            }
          }
        }
      }
    }

    stage('Deploy (docker compose up)') {
      steps {
        sh """
          # ensure compose uses the new tags (simple approach: env-file)
          export IMAGE_EXCHANGE_SERVICE=${REGISTRY}/exchange_service:${TAG}
          export IMAGE_USER_SERVICE=${REGISTRY}/user_service:${TAG}
          export IMAGE_GATEWAY_SERVICE=${REGISTRY}/gateway_service:${TAG}

          docker compose pull || true
          docker compose up -d --remove-orphans
        """
      }
    }
  }
  post {
    always {
      sh 'docker logout ${REGISTRY} || true'
    }
  }
}
