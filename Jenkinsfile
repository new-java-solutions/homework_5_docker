pipeline {
  agent any
  environment {
    REGISTRY = "35.223.206.102:5000"       // <— your Nexus Docker (hosted) port
    CRED_ID  = "nexus-docker"              // Jenkins credentials ID
    TAG      = "build-${env.BUILD_NUMBER}" // or use GIT_COMMIT
    SERVICES = "exchange_service,user_service,gateway_service"
  }
  triggers { pollSCM('') } // remove if using webhooks; this is a no-op poll
  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Build & Push images') {
      steps {
        script {
          def svcs = SERVICES.split(',')
          withCredentials([usernamePassword(credentialsId: CRED_ID, usernameVariable: 'U', passwordVariable: 'P')]) {
            sh "echo $P | docker login $REGISTRY -u $U --password-stdin"
          }
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
          export IMAGE_SERVICE_A=${REGISTRY}/exchange_service:${TAG}
          export IMAGE_SERVICE_B=${REGISTRY}/user_service:${TAG}
          export IMAGE_SERVICE_C=${REGISTRY}/gateway_service:${TAG}

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
