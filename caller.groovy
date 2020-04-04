node { 
    stage("Stage1"){ 
        echo "hello" 
} 
    stage("Stage2"){ 
        echo "hello" 
        build 'Timestamp'
} 
    stage("Stage3"){ 
        echo "Hello" 
        build "Script"
} 
    stage("Stage4"){ 
        build 'Template2' 
} 
    stage("Stage5"){ 
        echo "hello" 
    } 
} 