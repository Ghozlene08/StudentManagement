pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
        timeout(time: 30, unit: 'MINUTES')
    }

    environment {
        // Remplacez par votre adresse email pour recevoir les notifications en cas d'échec
        NOTIFICATION_EMAIL = 'ghozlene.nezhi@gmail.com'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Récupération du projet depuis GitHub...'
                checkout scm
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

        // Étape temporaire pour tester l'envoi d'email en cas d'échec — À SUPPRIMER après le test
        stage('Test email (à supprimer)') {
            steps {
                error 'Test envoi email : build volontairement en échec. Supprimez cette étape après vérification.'
            }
        }
    }

    post {
        success {
            echo 'Build réussi !'
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
    }
}
