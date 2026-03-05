pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
        timeout(time: 30, unit: 'MINUTES')
    }

    environment {
        NOTIFICATION_EMAIL = 'ghozlene.nezhi@gmail.com'
        DOCKER_IMAGE = 'ghozlene08/student-management'
        DOCKERHUB_CREDENTIALS = 'dockerhub-creds'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Récupération du projet depuis GitHub...'
                git branch: 'main', url: 'https://github.com/Ghozlene08/StudentManagement.git'
            }
        }

        stage('Tests unitaires') {
            steps {
                echo 'Exécution des tests unitaires...'
                sh 'mvn clean test -B'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                echo 'Création du livrable (JAR) dans target/...'
                sh 'mvn package -B -DskipTests'
            }
        }

        // ✅ STAGE 1 : Build de l'image Docker
        stage('Build Docker Image') {
            steps {
                echo 'Création de l\'image Docker...'
                script {
                    sh "docker build -t ${env.DOCKER_IMAGE}:${env.BUILD_NUMBER} ."
                    sh "docker tag ${env.DOCKER_IMAGE}:${env.BUILD_NUMBER} ${env.DOCKER_IMAGE}:latest"
                }
            }
        }

        // ✅ STAGE 2 : Push de l'image sur DockerHub
        stage('Push Docker Image') {
            steps {
                echo 'Push de l\'image sur Docker Hub...'
                script {
                    withCredentials([usernamePassword(
                            credentialsId: "${env.DOCKERHUB_CREDENTIALS}",
                            usernameVariable: 'DOCKER_USER',
                            passwordVariable: 'DOCKER_PASS'
                    )]) {
                        sh "echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin"
                        sh "docker push ${env.DOCKER_IMAGE}:${env.BUILD_NUMBER}"
                        sh "docker push ${env.DOCKER_IMAGE}:latest"
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✅ Build réussi ! Image disponible : ${env.DOCKER_IMAGE}:${env.BUILD_NUMBER}"
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
        failure {
            echo 'Build en échec - envoi de l\'email de notification...'
            emailext (
                    subject: "Échec du build Jenkins: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    body: """
                    Le build a échoué.

                    Job: ${env.JOB_NAME}
                    Build: #${env.BUILD_NUMBER}
                    URL: ${env.BUILD_URL}

                    Consultez les logs pour plus de détails.
                """,
                    to: "${env.NOTIFICATION_EMAIL}",
                    mimeType: 'text/plain'
            )
        }
        unstable {
            echo 'Build instable - envoi de l\'email de notification...'
            emailext (
                    subject: "Build instable Jenkins: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    body: """
                    Le build est instable (ex: tests échoués).

                    Job: ${env.JOB_NAME}
                    Build: #${env.BUILD_NUMBER}
                    URL: ${env.BUILD_URL}
                """,
                    to: "${env.NOTIFICATION_EMAIL}",
                    mimeType: 'text/plain'
            )
        }
        always {
            sh "docker logout || true"
        }
    }
}