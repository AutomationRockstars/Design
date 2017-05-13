pipeline {
	agent any
	stages {
		stage ('Checkout') {
		steps {	scm checkout }
		}
		
		stage ('Build and Test'){
		steps {	sh 'mvn clean install -Dnoui' }
		}

		stage('Deploy'){
		steps {	sh 'mvn source:jar deploy -DskipTests' }
		}
	}
	
}
