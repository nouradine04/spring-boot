pipeline {
    agent any

    environment {
        SONARQUBE = 'SonarQube'
        NEXUS_REPO = 'nexus-repository'
        KUBERNETES_CREDENTIALS = 'my-kubernetes-credentials' // Nom de votre credential Kubernetes dans Jenkins
        GITHUB_CREDENTIALS = credentials('GITHUB_TOKEN')  // Récupérer le token de Jenkins
    }

    stages {
        stage('Récupération du projet') {
            steps {
                script {
                    // Vérification si le répertoire existe et suppression si nécessaire
                    if (fileExists('spring-boot')) {
                        echo "Le répertoire 'spring-boot' existe déjà. Suppression en cours..."
                        sh 'rm -rf spring-boot'
                    }
                    // Clonage du projet depuis GitHub avec les credentials
                    git url: 'https://github.com/nouradine04/spring-boot.git', credentialsId: 'GITHUB_CREDENTIALS'
                }
            }
        }

        stage('Construction du projet') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Exécution des tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Analyse avec SonarQube') {
            steps {
                script {
                    // Lancer l'analyse SonarQube avec Maven
                    sh "mvn sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.login=${SONARQUBE_TOKEN}"
                }
            }
        }

        stage('Gestion de l\'Infrastructure avec Terraform') {
            steps {
                sh 'terraform init'
                sh 'terraform plan -out=tfplan'
                sh 'terraform apply -auto-approve tfplan'
            }
        }

        stage('Publication sur Nexus') {
            steps {
                nexusPublisher nexusInstanceId: NEXUS_REPO, nexusRepositoryId: 'maven-releases', file: 'target/*.jar'
            }
        }

        stage('Déploiement sur Kubernetes') {
            steps {
                withCredentials([kubeconfig(credentialsId: KUBERNETES_CREDENTIALS)]) {
                    sh 'kubectl apply -f k8s/deployment.yaml'
                    sh 'kubectl apply -f k8s/service.yaml'
                }
            }
        }

        stage('Monitoring avec Grafana') {
            steps {
                echo "Monitoring de l'application dans Grafana."
            }
        }
    }

    post {
        success {
            echo "Build et déploiement réussis !"
        }
        failure {
            echo "Échec du build. Consultez les logs pour plus de détails."
        }
    }
}
