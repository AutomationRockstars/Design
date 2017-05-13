pipeline {
	agent any
	stages {
		stage ('Checkout') {
			scm checkout
		}
		
		stage ('Build and Test'){
			sh 'mvn clean install -Dnoui'
		}

		stage('Deploy'){
			sh 'mvn source:jar deploy -DskipTests'
		}
	}
	
}
