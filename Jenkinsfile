pipeline {
	agent any
	stages {
		stage ('Clean'){
		steps { cleanWs cleanWhenFailure: false, deleteDirs: true, notFailBuild: true }
		}
		stage ('Checkout') {
		steps {	checkout scm }
		}
		
		stage ('Build and Test'){
		steps {	sh 'mvn clean install -Dgrid.url=http://172.17.0.1:4444/wd/hub -Dwebdriver.browser=chrome' }
		}

		stage('Deploy'){
		when { branch  'master' }
		steps {	sh 'mvn source:jar deploy -DskipTests' }
		}
	}
	
}
