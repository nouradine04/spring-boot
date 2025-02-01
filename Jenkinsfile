pipeline {
    agent any

    environment {
        // Variables d'environnement pour les credentials
        GITHUB_CREDENTIALS = credentials('GITHUB_TOKEN')  // Token GitHub
        SONARQUBE_TOKEN = credentials('sonarqube')         // Token SonarQube
        NEXUS_REPO = 'nexus-repository'                      // ID de l'instance Nexus
        KUBERNETES_CREDENTIALS = 'my-kubernetes-credentials' // Credentials Kubernetes
        MAVEN_OPTS = '-Xmx1024m -XX:MaxPermSize=512m'
    }

    stages {
        // --- Étape 1 : Récupération du projet ---
        stage('Recuperation projet') {
            steps {
                script {
                    // Supprimer le workspace si le projet existe déjà
                    if (fileExists('spring-boot')) {
                        echo "Le projet existe déjà. Suppression du dossier..."
                        deleteDir() // Supprime le contenu du workspace
                    }
                    echo "Clonage du projet depuis GitHub..."
                    // Clonage depuis la branche main
                    git branch: 'main', url: 'https://github.com/nouradine04/spring-boot.git'
                }
            }
        }
        
        // --- Étape 2 : Exécution de Maven dans Docker (Build, Tests, et SonarQube) ---
        stage('Build et Tests') {
            steps {
                script {
                    docker.image('maven:3.9.9-openjdk-11').inside {
                        // Vérifier la version de Maven
                        sh 'mvn -v'
                        
                        // Construction du projet
                        sh 'mvn clean package -DskipTests'
                        
                        // Exécution des tests
                        sh 'mvn test'
                    }
                }
            }
        }
        
        // --- Étape 3 : Analyse avec SonarQube ---
        stage('Analyse avec SonarQube') {
            steps {
                script {
                    docker.image('maven:3.9.9-openjdk-11').inside {
                        // Lancer l'analyse SonarQube avec Maven en utilisant le fichier settings.xml créé dynamiquement
                        // On peut créer un settings.xml si nécessaire pour Nexus ou d'autres configurations
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
                        sh "mvn -s ${env.WORKSPACE}/settings.xml sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.login=${SONARQUBE_TOKEN}"
                    }
                }
            }
        }
        
        // --- Étape 4 : Publication sur Nexus ---
        stage('Publication sur Nexus') {
            steps {
                script {
                    // Publication de l'artefact sur Nexus via le plugin Nexus Publisher (ceci s'exécute sur l'agent Jenkins)
                    nexusPublisher nexusInstanceId: NEXUS_REPO, 
                                   nexusRepositoryId: 'maven-releases', 
                                   packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: '', filePath: 'target/*.jar']]]]
                }
            }
        }
        
        // --- Étape 5 : Déploiement avec Terraform ---
        stage('Terraform') {
            steps {
                script {
                    // Ces commandes s'exécutent sur l'agent Jenkins. Assure-toi que Terraform est installé sur l'agent ou dans l'image si nécessaire.
                    sh 'terraform init'
                    sh 'terraform plan'
                    sh 'terraform apply -auto-approve'
                }
            }
        }
        
        // --- Étape 6 : Déploiement sur Kubernetes ---
        stage('Déploiement sur Kubernetes') {
            steps {
                script {
                    // Utilisation des credentials Kubernetes pour récupérer le fichier KUBECONFIG
                    withCredentials([file(credentialsId: KUBERNETES_CREDENTIALS, variable: 'KUBECONFIG')]) {
                        sh 'kubectl apply -f k8s/deployment.yaml'
                        sh 'kubectl apply -f k8s/service.yaml'
                    }
                }
            }
        }
        
        // --- Étape 7 : Monitoring avec Grafana ---
        stage('Monitoring avec Grafana') {
            steps {
                echo "Monitoring de l'application dans Grafana"
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
