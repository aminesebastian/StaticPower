pipeline {
    agent any
    environment {
        GRADLE_OPTS = "-Dorg.gradle.daemon=false" // This environment variable aims to disable Gradle Daemon for piles of incompatible Gradle Daemons existence.
    }
    stages {
        stage('Pre-Build') {
            steps {
                sh 'chmod +x gradlew'
            }
        }
        stage('Build') {
            steps {
                sh './gradlew build'
            }
        }
        stage('Test') {
            steps {
                sh './gradlew test'
            }
        }
    }
    post {
        success {
            archiveArtifacts artifacts: 'build/libs/**/*.jar', fingerprint: true
        }
        cleanup {
            script {
                if (env.BRANCH_NAME == 'master') {
                    deleteDir()
                } else {
                    dir('build/libs') {
                        deleteDir()
                    }
                }
            }
        }
    }
}
