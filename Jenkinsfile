pipeline {
    agent any

    environment {
        SONARQUBE = 'SonarQube'
        NEXUS_REPO = 'nexus-repository'
        KUBERNETES_CREDENTIALS = 'my-kubernetes-credentials' // Nom de ton credential Kubernetes dans Jenkins
    }

    stages {
        stage('recuperation projet') {
            steps {
                git branch: 'main', url: 'https://github.com/nouradine04/springboot-project.git'
            }
        }

        stage('contruction projet') {
            steps {
                sh './mvnw clean package -DskipTests'
            }
        }

        stage(' Analyse avec SonarQube ') {
                    steps {
                        script {
                            // Lancer l'analyse SonarQube avec Maven
                            sh 'mvn sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.login=squ_0108ac01d1af24a0bb52762304c294de1811bbce'
                        }

                }
            }
        }

        stage('Test') {
            steps {
                sh './mvnw test'
            }
        }

        stage('recuperation  Nexus') {
            steps {
                script {
                    nexusPublisher nexusInstanceId: 'nexus-repository', nexusRepositoryId: 'maven-releases', file: 'target/*.jar'
                }
            }
        }

        stage('Terraform ') {
            steps {
                script {
                    sh 'terraform init'
                    sh 'terraform plan'
                    sh 'terraform apply -auto-approve'
                }
            }
        }

        stage('Deploiement  Kubernetes') {
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
                // Exemple de vérification avec Grafana (tu peux adapter en fonction de ton monitoring)
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
