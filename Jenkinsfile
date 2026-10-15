pipeline {
    // Use a Linux agent with Java 17, Maven, and Docker access
    // on the target EC2 host.
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        skipDefaultCheckout()
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Maven Build and Tests') {
            steps {
                sh 'mvn -B -ntp clean verify'
            }
            post {
                always {
                    junit testResults: '**/target/surefire-reports/*.xml',
                          allowEmptyResults: true
                }
                success {
                    archiveArtifacts artifacts: 'target/java-cicd-lab.jar',
                                     fingerprint: true
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh '''
                    set -eu

                    docker build \
                      -f devops/docker/Dockerfile \
                      -t java-cicd-lab:$BUILD_NUMBER \
                      .
                '''
            }
        }

        stage('Run Application') {
            steps {
                sh '''
                    set -eu

                    # Replace only this lab application's container.
                    if docker container inspect java-cicd-lab >/dev/null 2>&1; then
                        docker rm -f java-cicd-lab
                    fi

                    docker run -d \
                      --name java-cicd-lab \
                      --restart unless-stopped \
                      -p 8081:8080 \
                      java-cicd-lab:$BUILD_NUMBER
                '''
            }
        }
    }
}
