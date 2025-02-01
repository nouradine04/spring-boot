pipeline {
    agent {
        docker {
            image 'maven:3.9.9-jdk-11' // Utilisation de l'image Docker officielle de Maven
            args '-v /root/.m2:/root/.m2' // Montée du dossier .m2 pour le cache des dépendances
        }
    }

    stages {
        stage('Récupération du projet') {
            steps {
                script {
                    // Clonage du projet depuis GitHub
                    git 'https://github.com/nouradine04/spring-boot.git'
                }
            }
        }

        stage('Construction du projet') {
            steps {
                // Utilisation de Maven pour construire le projet
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Exécution des tests') {
            steps {
                // Lancer les tests avec Maven
                sh 'mvn test'
            }
        }

        stage('Analyse avec SonarQube') {
            steps {
                script {
                    // Lancer l'analyse SonarQube avec Maven
                    sh 'mvn sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.login=${SONARQUBE_TOKEN}'
                }
            }
        }

        stage('Gestion de l\'Infrastructure avec Terraform') {
            steps {
                script {
                    // Initialisation, planification et application de Terraform
                    sh 'terraform init'
                    sh 'terraform plan -out=tfplan'
                    sh 'terraform apply -auto-approve tfplan'
                }
            }
        }

        stage('Publication sur Nexus') {
            steps {
                script {
                    // Publication de l'artifact sur Nexus
                    nexusPublisher nexusInstanceId: 'nexus-repository', nexusRepositoryId: 'maven-releases', file: 'target/*.jar'
                }
            }
        }

        stage('Déploiement sur Kubernetes') {
            steps {
                script {
                    withCredentials([kubeconfig(credentialsId: KUBERNETES_CREDENTIALS)]) {
                        sh 'kubectl apply -f k8s/deployment.yaml'
                        sh 'kubectl apply -f k8s/service.yaml'
                    }
                }
            }
        }

        stage('Monitoring avec Grafana') {
            steps {
                echo 'Monitoring de l\'application dans Grafana.'
            }
        }
    }

    post {
        success {
            echo 'Build et déploiement réussis !'
        }
        failure {
            echo 'Échec du build. Consultez les logs pour plus de détails.'
        }
    }
}
