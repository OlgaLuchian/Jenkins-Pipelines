node { 
  properties([
      // Below line sets "Discard Builds more than 5"
      buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')), 
      
      // Below line triggers this job every minute
      pipelineTriggers([pollSCM('* * * * * ')]),
      parameters([choice(choices: [
         'dev1.olgaandolga.com', 
         'prod1.olgaandolga.com', 
         'qa1.olgaandolga.com', 
         'stage1.olgaandolga.com'],
          description: 'Please choose an environment ', 
          name: 'ENVIR')]),
      ])

       // Pulls a Repo from developer
   stage("Pull Repo"){ 
     checkout([$class: 'GitSCM', branches: [[name: '*/FarrukH']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/farrukh90/cool_website.git']]])
   } 

      // Installs web server on different environment
   stage("Install Prerequisites"){ 
       sh """ 
       ssh centos@prod1.olgaandolga.com      sudo yum install httpd -y
       """


   }  // Copies over developers files to different environment
   stage("Copy Artifacts"){ 
      sh """
      scp -r *  centos@prod1.olgaandolga.com:/tmp
       ssh centos@prod1.olgaandolga.com      sudo yum install httpd -y
       ssh centos@prod1.olgaandolga.com      sudo cp -r /tmp/style.css /var/www/html/
       ssh centos@prod1.olgaandolga.com      sudo chown centos:centos /var/www/html/
       ssh centos@prod1.olgaandolga.com      sudo chmod 777 /var/www/html/*
       ssh centos@prod1.olgaandolga.com      sudo systemctl restart httpd
      """ 
   } 
      
      // Restarts web server
   stage("Restart Web Server"){ 
      sh  "ssh centos@prod1.olgaandolga.com    sudo systemctl restart httpd" 
   } 
       
      // Sends a message to slack
   stage("Stage5"){ 
       slackSend color: '#BADA55', message: 'Hello, World!'  
   } 
} 





