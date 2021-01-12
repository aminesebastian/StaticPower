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
                
                discordSend description: "New mod version available! Use Username: Minecraft and Password: Minecraft to gain access! \n\n Changes:\n" + getChangeLog(passedBuilds), footer: "Patch Version: " + currentBuild.number, link: "http://jenkins.suburbandigital.com:8080/job/Static%20Power/job/development/lastSuccessfulBuild/artifact/build/libs/", result: currentBuild.currentResult, title: JOB_NAME, webhookURL: "https://discord.com/api/webhooks/798478687975768066/okWARb5o92cnKK2c6-ebbNA8P9Mu6Ss3Ol8z8XYsnJ88VXRNMtVE753GQZTiCV9u0q1Y"
            }
        }
    }
}


@NonCPS
def getChangeLog(passedBuilds) {
    def log = ""
    for (int x = 0; x < passedBuilds.size(); x++) {
        def currentBuild = passedBuilds[x];
        def changeLogSets = currentBuild.rawBuild.changeSets
        for (int i = 0; i < changeLogSets.size(); i++) {
            def entries = changeLogSets[i].items
            for (int j = 0; j < entries.length; j++) {
                def entry = entries[j]
                log += "* ${entry.msg} by ${entry.author} \n"
            }
        }
    }
    return log;
}
