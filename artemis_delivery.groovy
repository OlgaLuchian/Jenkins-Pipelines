node {
	properties(
		[parameters(
			[choice(choices: 
			[
				'version/0.1', 
				'version/0.2', 
				'version/0.3', 
				'version/0.4', 
				'version/0.5'], 
	description: 'Which version of the app should I deploy? ', 
	name: 'Version')])])
	stage("Stage1"){
		timestamps {
			ws {
                checkout([$class: 'GitSCM', branches: [[name: '${Version}']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/farrukh90/artemis.git']]])		
        }	
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


}