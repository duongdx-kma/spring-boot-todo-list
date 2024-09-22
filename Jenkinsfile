pipeline{
    agent any

    tools {
        maven "MAVEN3"
        jdk "OPEN_JDK17"
    }

    environment {
        NEXUS_DOMAIN = "https://nexus.duongdx.com"
        SNAPSHOT_REPO = "custom-maven-snapshots"
        RELEASE_REPO = "custom-maven-releases"
        PROXY_REPO = "custom-maven-proxy"
        NEXUS_GROUP_REPO = "custom-maven-group"
        NEXUS_JENKINS_CREDENTIAL = "maven_login"
        SONAR_SERVER = "duongdx_sonarqube_server"
        SONAR_SCANNER = "sonarscanner6"
    }

    stages{
        stage('BUILD') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'maven_login', usernameVariable: 'NEXUS_USERNAME', passwordVariable: 'NEXUS_PASSWORD')]) {
                    sh 'env'

                    sh 'mvn -s settings.xml clean install -DskipTests'
                }
            }
            // post {
            //     success {
            //         echo 'Now Archiving...'
            //         archiveArtifacts artifacts: '**/target/*.war', fingerprint: true
            //         stash name: 'app-war', includes: '**/target/*.war' // save the artifact for Deploy State
            //     }
            // }
        }

        stage('UNIT TEST'){
            steps {
                withCredentials([usernamePassword(credentialsId: 'maven_login', usernameVariable: 'NEXUS_USERNAME', passwordVariable: 'NEXUS_PASSWORD')]) {
                    sh 'mvn -s settings.xml test'
                }
            }
        }

        stage('INTEGRATION TEST'){
            steps {
                withCredentials([usernamePassword(credentialsId: 'maven_login', usernameVariable: 'NEXUS_USERNAME', passwordVariable: 'NEXUS_PASSWORD')]) {
                    sh 'mvn -s settings.xml verify -DskipUnitTests'
                }
            }
        }
		
        stage ('CODE ANALYSIS WITH CHECKSTYLE'){
            steps {
                withCredentials([usernamePassword(credentialsId: 'maven_login', usernameVariable: 'NEXUS_USERNAME', passwordVariable: 'NEXUS_PASSWORD')]) {
                    sh 'mvn -s settings.xml checkstyle:checkstyle'
                }
            }
            post {
                success {
                    echo 'Generated Analysis Result'
                }
            }
        }

        stage('CODE ANALYSIS with SONARQUBE') {
		  environment {
             scannerHome = tool "${SONAR_SCANNER}"
          }

          steps {
            withSonarQubeEnv("${SONAR_SERVER}") {
                sh '''${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=spring-boot-todo-list \
                    -Dsonar.projectName=spring-boot-todo-list \
                    -Dsonar.projectVersion=1.0 \
                    -Dsonar.sources=src/ \
                    -Dsonar.java.binaries=target/classes,target/test-classes \
                    -Dsonar.junit.reportsPath=target/surefire-reports/ \
                    -Dsonar.jacoco.reportsPath=target/jacoco.exec \
                    -Dsonar.java.checkstyle.reportPaths=target/checkstyle-result.xml'''
            }

            // // config SonarQube Quality Gate
            // timeout(time: 10, unit: 'MINUTES') {
            //    waitForQualityGate abortPipeline: true
            // }
          }
        }
    }

    post{
        always{
            echo "========always========"
        }
        success{
            echo "========pipeline executed successfully ========"
        }
        failure{
            echo "========pipeline execution failed========"
        }
    }
}