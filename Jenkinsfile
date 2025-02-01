pipeline {
    agent {
        // Utilise un agent docker avec une image Maven (avec OpenJDK) pour les étapes Maven
        docker {
            image 'maven:3.9.9-openjdk-11'
            // Tu peux ajouter d'autres options si nécessaire, par ex. args '--privileged'
        }
    }

    environment {
        // Maven est déjà installé dans l'image Docker utilisée ci-dessus, donc pas besoin de MAVEN_HOME
        MAVEN_OPTS = '-Xmx1024m -XX:MaxPermSize=512m'
        GITHUB_CREDENTIALS = credentials('GITHUB_TOKEN')
        SONARQUBE_TOKEN = credentials('sonarqube')
        NEXUS_REPO = 'nexus-repository'
        KUBERNETES_CREDENTIALS = 'my-kubernetes-credentials'
    }

    stages {
        stage('Recuperation projet') {
            steps {
                script {
                    // Si le dossier existe, on le supprime pour un clone propre
                    if (fileExists('spring-boot')) {
                        echo "Le projet existe déjà. Suppression du dossier..."
                        deleteDir() 
                    }
                    echo "Clonage du projet depuis GitHub..."
                    git branch: 'main', url: 'https://github.com/nouradine04/spring-boot.git'
                }
            }
        }
        
        stage('Build et Tests') {
            steps {
                // Ces commandes s'exécutent dans le conteneur Docker qui a Maven installé
                sh 'mvn -v'  // Vérifie la version de Maven
                sh 'mvn clean package -DskipTests'
                sh 'mvn test'
            }
        }
        
        stage('Analyse avec SonarQube') {
            steps {
                script {
                    // On recrée éventuellement un settings.xml si nécessaire pour la configuration de Nexus
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
        
        stage('Publication sur Nexus') {
            steps {
                script {
                    nexusPublisher nexusInstanceId: NEXUS_REPO, 
                                   nexusRepositoryId: 'maven-releases', 
                                   packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: '', filePath: 'target/*.jar']]]]
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
