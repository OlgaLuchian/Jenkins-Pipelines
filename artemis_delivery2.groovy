node {
	properties(
		[parameters(
			[choice(choices: 
			[
				'0.1', 
				'0.2', 
				'0.3', 
				'0.4', 
				'0.5',
                '0.6', 
                '0.7', 
                '0.8', 
                '0.9', 
                '10', 
                ], 
	    description: 'Which version of the app should I deploy? ', 
	    name: 'Version'),
    choice(choices: 
    [
	
        'dev1.olgaandolga.com', 
        'prod1.olgaandolga.com', 
        'qa1.olgaandolga.com', 
        'stage1.olgaandolga.com'],
    description: 'Please choose an environment ', 
    name: 'ENVIR')])])
	    stage("Stage1"){
		    timestamps {
			    ws {
                    checkout([$class: 'GitSCM', branches: [[name: '${Version}']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/farrukh90/artemis.git']]])		
        	
        }
    }
	stage("Get Credentials"){
		timestamps {
			ws{
				sh '''
                   aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 777042527031.dkr.ecr.us-east-1.amazonaws.com/artemis					
                   '''
		    }
        }
    }
    stage("Build Docker Image"){ 
        timestamps { 
            ws { 
                sh ''' 
                   docker build -t artemis:${Version} . 
                   ''' 
            } 
        } 
    }

    stage("Tag Image"){ 
        timestamps { 
            ws { 
                sh ''' 
                   docker tag artemis:${Version} 777042527031.dkr.ecr.us-east-1.amazonaws.com/artemis:${Version}                  
                   ''' 
                } 
            } 
        }
    stage("Push Image"){ 
        timestamps { 
            ws { 
                sh ''' 
                   docker push 777042527031.dkr.ecr.us-east-1.amazonaws.com/artemis:${Version} 
                   ''' 
                } 
            } 
        } 
        stage("Send slack notifications"){ 
            timestamps { 
                ws { 
                    echo "Slack" 
                    //slackSend color: '#BADA55', message: 'Hello, World!' 
                } 
            } 
        } 
    }  
        stage("Clean Up"){ 
        timestamps { 
            ws { 
                try { 
                    sh ''' 
                    #!/bin/bash 
                    IMAGES=$(ssh centos@${ENVIR}.olgaandolga.com docker ps -aq)  
                    for i in \$IMAGES; do 
                        ssh centos@${ENVIR}.olgaandolga.com docker stop \$i 
                        ssh centos@${ENVIR}.olgaandolga.com docker rm \$i 
                done  
                ''' 
                } catch(e) { 
                    println("Script failed with error: ${e}") 
                } 
            } 
        } 
    } 
        stage("Run Container"){ 
            timestamps { 
                ws { 
                    sh ''' 
                       ssh centos@dev1.olgaandolga.com docker run -dti -p 5001:5000 713287746880.dkr.ecr.us-east-1.amazonaws.com/artemis:${Version} 
                       ''' 
                    } 
                } 
            } 
        } 


