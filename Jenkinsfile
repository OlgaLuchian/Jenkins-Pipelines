pipeline {
  agent any
  stages {
    stage('pull repo') {
      steps {
        git 'https://github.com/OlgaLuchian/jenkins-pipelines'
      }
    }

    stage('Stage3') {
      steps {
        sh 'echo "Hello"'
        echo 'Stage3'
      }
    }

  }
}