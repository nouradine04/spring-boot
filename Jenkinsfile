pipeline {
    // Utilisation d'un agent Docker avec l'image officielle Maven 3.9.9 et OpenJDK 11
    agent {
        docker {
            image 'maven:3.9.9-openjdk-11'
            // Optionnel : ajouter des arguments au conteneur si nécessaire (par ex., montage de volume)
            // args '--privileged'
        }
    }

    environment {
        // Maven est déjà installé dans l'image, donc on n'a pas besoin de définir MAVEN_HOME
        MAVEN_OPTS = '-Xmx1024m -XX:MaxPermSize=512m'
        // Credentials (assure-toi que ces credentials sont bien créés dans Jenkins)
        GITHUB_CREDENTIALS = credentials('GITHUB_TOKEN')
        SONARQUBE_TOKEN = credentials('sonarqube')
        NEXUS_REPO = 'nexus-repository'
        KUBERNETES_CREDENTIALS = 'my-kubernetes-credentials'
    }

    stages {
        // --- Étape 1 : Récupération du projet ---
        stage('Recuperation projet') {
            steps {
                script {
                    // Si le dossier "spring-boot" existe, on le supprime pour forcer un clone propre
                    if (fileExists('spring-boot')) {
                        echo "Le projet existe déjà. Suppression du dossier..."
                        deleteDir() // Supprime tout le contenu du workspace
                    }
                    echo "Clonage du projet depuis GitHub..."
                    // Clonage depuis la branche main
                    git branch: 'main', url: 'https://github.com/nouradine04/spring-boot.git'
                }
            }
        }

        // --- Étape 2 : Configuration de Maven ---
        stage('Configuration de Maven') {
            steps {
                script {
                    // Vérification que Maven est accessible dans le conteneur Docker
                    sh 'mvn -v'
                    // Écriture d'un fichier settings.xml pour Nexus
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

        // --- Étape 3 : Construction du projet ---
        stage('Construction projet') {
            steps {
                // Utilisation de Maven pour construire le projet
                sh 'mvn clean package -DskipTests'
            }
        }

        // --- Étape 4 : Exécution des tests ---
        stage('Tests') {
            steps {
                // Exécution des tests avec Maven en utilisant le settings.xml personnalisé
                sh 'mvn -s ${WORKSPACE}/settings.xml test'
            }
        }

        // --- Étape 5 : Analyse avec SonarQube ---
        stage('Analyse avec SonarQube') {
            steps {
                // Lancement de l'analyse SonarQube avec Maven
                // On utilise directement la variable SONARQUBE_TOKEN (définie dans l'environnement)
                sh "mvn -s ${WORKSPACE}/settings.xml sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.login=${SONARQUBE_TOKEN}"
            }
        }

        // --- Étape 6 : Publication sur Nexus ---
        stage('Publication sur Nexus') {
            steps {
                script {
                    // Publication de l'artefact sur Nexus
                    nexusPublisher nexusInstanceId: NEXUS_REPO, 
                                   nexusRepositoryId: 'maven-releases', 
                                   packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: '', filePath: 'target/*.jar']]]]
                }
            }
        }

        // --- Étape 7 : Déploiement avec Terraform ---
        stage('Terraform') {
            steps {
                script {
                    sh 'terraform init'
                    sh 'terraform plan'
                    sh 'terraform apply -auto-approve'
                }
            }
        }

        // --- Étape 8 : Déploiement sur Kubernetes ---
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

        // --- Étape 9 : Monitoring avec Grafana ---
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
