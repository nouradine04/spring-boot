pipeline {
    agent any

    environment {
        // Configuration de Maven
        MAVEN_HOME = '/Users/mac/Desktop/apache-maven-3.9.9'  // Chemin vers Maven
        PATH = "${MAVEN_HOME}/bin:${PATH}"  // Ajouter Maven au PATH
        MAVEN_OPTS = '-Xmx1024m -XX:MaxPermSize=512m'  // Allouer plus de mémoire à Maven

        // Autres variables d'environnement
        GITHUB_CREDENTIALS = credentials('GITHUB_TOKEN')  // Token GitHub
        SONARQUBE_TOKEN = credentials('SONARQUBE_TOKEN')  // Token SonarQube
        NEXUS_REPO = 'nexus-repository'  // ID de l'instance Nexus
        KUBERNETES_CREDENTIALS = 'my-kubernetes-credentials'  // Credentials Kubernetes
    }

    stages {
        // Étape 1 : Récupération du projet
        stage('Récupération du projet') {
            steps {
                script {
                    if (!fileExists('spring-boot')) {
                        git url: 'https://github.com/nouradine04/spring-boot.git', credentialsId: GITHUB_CREDENTIALS
                    } else {
                        echo "Le projet existe déjà."
                    }
                }
            }
        }

        // Étape 2 : Configuration de Maven
        stage('Configuration de Maven') {
            steps {
                script {
                    // Vérifier que Maven est installé
                    sh "'${MAVEN_HOME}/bin/mvn' -v"
                    
                    // Configurer le fichier settings.xml pour Nexus (si nécessaire)
                    writeFile file: "${env.WORKSPACE}/settings.xml", text: """
                    <settings>
                        <mirrors>
                            <mirror>
                                <id>nexus</id>
                                <url>http://localhost:8081/repository/maven-public/</url>
                                <mirrorOf>*</mirrorOf>
                            </mirror>
                        </mirrors>
                    </settings>
                    """
                }
            }
        }

        // Étape 3 : Construction du projet
        stage('Construction du projet') {
            steps {
                script {
                    // Exécuter Maven avec le fichier settings.xml personnalisé
                    sh "'${MAVEN_HOME}/bin/mvn' -s ${env.WORKSPACE}/settings.xml clean package -DskipTests"
                }
            }
        }

        // Étape 4 : Exécution des tests
        stage('Tests') {
            steps {
                script {
                    // Exécuter les tests avec Maven
                    sh "'${MAVEN_HOME}/bin/mvn' -s ${env.WORKSPACE}/settings.xml test"
                }
            }
        }

        // Étape 5 : Analyse avec SonarQube
        stage('Analyse avec SonarQube') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'SONARQUBE_TOKEN', variable: 'SONARQUBE_TOKEN')]) {
                        sh "'${MAVEN_HOME}/bin/mvn' -s ${env.WORKSPACE}/settings.xml sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.login=${SONARQUBE_TOKEN}"
                    }
                }
            }
        }

        // Étape 6 : Publication sur Nexus
        stage('Publication sur Nexus') {
            steps {
                script {
                    nexusPublisher nexusInstanceId: NEXUS_REPO, nexusRepositoryId: 'maven-releases', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: '', filePath: 'target/*.jar']]]]
                }
            }
        }

        // Étape 7 : Déploiement avec Terraform
        stage('Terraform') {
            steps {
                script {
                    sh 'terraform init'
                    sh 'terraform plan'
                    sh 'terraform apply -auto-approve'
                }
            }
        }

        // Étape 8 : Déploiement sur Kubernetes
        stage('Déploiement sur Kubernetes') {
            steps {
                script {
                    withCredentials([file(credentialsId: KUBERNETES_CREDENTIALS, variable: 'KUBECONFIG')]) {
                        sh 'kubectl apply -f k8s/deployment.yaml'
                        sh 'kubectl apply -f k8s/service.yaml'
                    }
                }
            }
        }

        // Étape 9 : Monitoring avec Grafana
        stage('Monitoring avec Grafana') {
            steps {
                echo 'Monitoring de l\'application dans Grafana'
            }
        }
    }

    post {
        success {
            echo 'Build et déploiement réussis !'
        }
        failure {
            echo 'Le build a échoué. Veuillez vérifier les logs.'
        }
    }
}
