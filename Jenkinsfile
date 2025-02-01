pipeline {
    agent any

    environment {
        SONARQUBE = 'sonarqube'
        NEXUS_REPO = 'nexus-repository'
        KUBERNETES_CREDENTIALS = 'my-kubernetes-credentials' // Nom de ton credential Kubernetes dans Jenkins
        GITHUB_CREDENTIALS = credentials('GITHUB_TOKEN')  // Récupérer le token de Jenkins
        MAVEN_HOME = tool name: 'maven', type: 'Tool'  // Assurez-vous que Maven est installé et configuré dans Jenkins
    }

    stages {
        stage('recuperation projet') {
            steps {
                sh "git clone https://${GITHUB_CREDENTIALS}@github.com/nouradine04/spring-boot.git"
            }
        }

        stage('contruction projet') {
            steps {
                // Utilisation de Maven installé et configuré dans Jenkins
                sh "'${MAVEN_HOME}/bin/mvn' clean package -DskipTests"
            }
        }

        stage('Analyse avec SonarQube') {
            steps {
                script {
                    // Lancer l'analyse SonarQube avec Maven
                    sh "'${MAVEN_HOME}/bin/mvn' sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.login=${SONARQUBE_TOKEN}"
                }
            }
        }

        stage('Test') {
            steps {
                sh "'${MAVEN_HOME}/bin/mvn' test"
            }
        }

        stage('recuperation Nexus') {
            steps {
                script {
                    nexusPublisher nexusInstanceId: 'nexus-repository', nexusRepositoryId: 'maven-releases', file: 'target/*.jar'
                }
            }
        }

        stage('Terraform') {
            steps {
                script {
                    sh 'terraform init'
                    sh 'terraform plan'
                    sh 'terraform apply -auto-approve'
                }
            }
        }

        stage('Deploiement Kubernetes') {
            steps {
                script {
                    // Utilisation de kubectl pour déployer sur Kubernetes
                    sh 'kubectl apply -f k8s/deployment.yaml'
                    sh 'kubectl apply -f k8s/service.yaml'
                }
            }
        }

        stage('Monitoring avec Grafana') {
            steps {
                echo 'Monitoring app in Grafana'
            }
        }
    }

    post {
        success {
            echo 'Build and deploy successful!'
        }
        failure {
            echo 'Build failed. Check logs.'
        }
    }
}
