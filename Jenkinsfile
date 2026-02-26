// Cross-platform: uses 'sh' on Linux/Unix agents and 'bat' on Windows agents
pipeline {
  agent any
  tools {
    jdk 'JDK-17'
    maven 'Maven-3.9'
  }
  options {
    buildDiscarder(logRotator(numToKeepStr: '30'))
    timestamps()
  }
  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }
    stage('Build & Test') {
      steps {
        script {
          if (isUnix()) {
            sh 'mvn clean verify'
          } else {
            bat 'mvn clean verify'
          }
        }
      }
    }
    stage('Publish JaCoCo') {
      steps {
        jacoco(
          execPattern: 'target/jacoco.exec',
          skipCopyOfSrcFiles: true
        )
      }
    }
    stage('Publish SpotBugs') {
      steps {
        recordIssues(
          tools: [spotBugs(pattern: 'target/spotbugsXml.xml')]
        )
      }
    }
  }
  post {
    always {
      archiveArtifacts artifacts: 'target/site/jacoco/**/*,target/spotbugsXml.xml', allowEmptyArchive: true
    }
  }
}
