node { 
  properties([
      // Below line sets "Discard Builds more than 5"
      buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')), 
      
      // Below line triggers this job every minute
      pipelineTriggers([pollSCM('* * * * * ')])
      ])


   stage("Pull Repo"){ 
     checkout([$class: 'GitSCM', branches: [[name: '*/FarrukH']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/farrukh90/cool_website.git']]])
   } 

   stage("Install Prerequisites"){ 
       sh """ 
       ssh centos@dev1.olgaandolga.com      sudo yum install httpd -y
       """
   } 
   stage("Copy Artifacts"){ 
      sh """
      scp -r *  centos@dev1.olgaandolga.com:/tmp
       ssh centos@dev1.olgaandolga.com      sudo yum install httpd -y
       ssh centos@dev1.olgaandolga.com      sudo cp -r /tmp/style.css /var/www/html/
       ssh centos@dev1.olgaandolga.com      sudo chown centos:centos /var/www/html/
       ssh centos@dev1.olgaandolga.com      sudo chmod 777 /var/www/html/*
       ssh centos@dev1.olgaandolga.com      sudo systemctl restart httpd
      """ 
   } 

   stage("Restart Web Server"){ 
      sh  "ssh centos@dev1.olgaandolga.com    sudo systemctl restart httpd" 
   } 

   stage("Stage5"){ 
       slackSend color: '#BADA55', message: 'Hello, World!'  
   } 
} 





