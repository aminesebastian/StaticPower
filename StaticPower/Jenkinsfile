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
                sh './gradlew build -Pbuild_number=' + currentBuild.number
            }
        }
        stage('Test') {
            steps {
                sh './gradlew test -Pbuild_number=' + currentBuild.number
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
                sendDiscordMessage()
            }
        }
    }
}

@NonCPS
def sendDiscordMessage() {
    def commitMessages = ""
    def changeLogSets = currentBuild.changeSets
    for (int i = 0; i < changeLogSets.size(); i++) {
        def entries = changeLogSets[i].items
        for (int j = 0; j < entries.length; j++) {
            def entry = entries[j]
            commitMessages = commitMessages + "${entry.author} *${entry.msg}*\n" 
        }
    }
    discordSend description: "New mod version available! Use Username: Minecraft and Password: Minecraft to gain access! \n\nChangelog:\n${commitMessages}", footer: "Patch Version: " + currentBuild.number, link: "https://jenkins.suburbandigital.com/job/Static%20Power/job/development/lastSuccessfulBuild/artifact/build/libs/", result: currentBuild.currentResult, title: JOB_NAME, enableArtifactList: true, webhookURL: "https://discord.com/api/webhooks/798478687975768066/okWARb5o92cnKK2c6-ebbNA8P9Mu6Ss3Ol8z8XYsnJ88VXRNMtVE753GQZTiCV9u0q1Y"      
}
