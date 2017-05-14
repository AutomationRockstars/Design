pipeline {
	agent any
	stages {
		stage ('Checkout') {
		steps {	checkout scm }
		}
		
		stage ('Build and Test'){
		steps {	sh 'mvn clean install -Dgrid.url=http://localhost:4444/wd/hub' }
		}

		stage('Deploy'){
		when { branch  'master' }
		steps {	sh 'mvn source:jar deploy -DskipTests' }
		}
	}
	
}
