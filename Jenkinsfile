/*
 * <!--
 *     Copyright (c) 2015-2019 Automation RockStars Ltd.
 *     All rights reserved. This program and the accompanying materials
 *     are made available under the terms of the Apache License v2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Contributors:
 *         Automation RockStars
 *  -->
 */

pipeline {
    agent any
    stages {
        stage('Clean') {
            steps { cleanWs cleanWhenFailure: false, deleteDirs: true, notFailBuild: true }
        }
        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Build and Test') {
            steps { sh 'mvn clean install -Dgrid.url=http://172.17.0.1:4444/wd/hub -Dwebdriver.browser=chrome' }
        }

        stage('Deploy') {
            when { branch 'master' }
            steps { sh 'mvn source:jar deploy -DskipTests' }
        }
        stage('Results') {
            steps { junit '**/target/surefire-reports/*.xml' }
        }
    }

}
