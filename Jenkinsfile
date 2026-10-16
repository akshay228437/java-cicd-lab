pipeline {
    // Use a Linux agent with Java 17, Maven, and Docker access
    // on the target EC2 host.
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        skipStagesAfterUnstable()
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

        stage('SonarQube Analysis') {
            steps {
                // Server name under Manage Jenkins > System > SonarQube servers.
                // The token must be a Secret text credential with ID sonarqube.
                withSonarQubeEnv(installationName: 'sonarqube', credentialsId: 'sonarqube') {
                    sh '''
                        set +x
                        set -eu
                        export SONAR_TOKEN="$SONAR_AUTH_TOKEN"

                        mvn -B -ntp \
                          org.sonarsource.scanner.maven:sonar-maven-plugin:5.6.0.6792:sonar \
                          -Dsonar.projectKey=java-cicd-lab \
                          -Dsonar.projectName=java-cicd-lab \
                          -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                    '''
                }
            }
        }

        stage('SonarQube Quality Gate') {
            steps {
                // SonarQube must send its webhook to:
                // https://<jenkins-host>/sonarqube-webhook/
                timeout(time: 10, unit: 'MINUTES') {
                    script {
                        def gate = waitForQualityGate()
                        if (gate.status != 'OK') {
                            error("SonarQube Quality Gate failed: ${gate.status}")
                        }
                    }
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
