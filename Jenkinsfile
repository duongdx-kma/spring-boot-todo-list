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
        NEXUS_VERSION = 3
        NEXUS_PROTOCOL = "http"
        NEXUS_URL = "https://nexus.duongdx.com"
        ARTIFACT_VERSION = "${env.BUILD_ID}"

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

            // config SonarQube Quality Gate
            timeout(time: 10, unit: 'MINUTES') {
               waitForQualityGate abortPipeline: true
            }
          }
        }

        stage("Publish to Nexus Repository Manager") {
            steps {
                script {
                    pom = readMavenPom file: "pom.xml";
                    filesByGlob = findFiles(glob: "target/*.${pom.packaging}");
                    echo "${filesByGlob[0].name} ${filesByGlob[0].path} ${filesByGlob[0].directory} ${filesByGlob[0].length} ${filesByGlob[0].lastModified}"
                    artifactPath = filesByGlob[0].path;
                    artifactExists = fileExists artifactPath;
                    if(artifactExists) {
                        echo "*** File: ${artifactPath}, group: ${pom.groupId}, packaging: ${pom.packaging}, version ${pom.version} ARTVERSION";
                        nexusArtifactUploader(
                            nexusVersion: NEXUS_VERSION,
                            protocol: NEXUS_PROTOCOL,
                            nexusUrl: NEXUS_URL,
                            groupId: NEXUS_GROUP_REPO,
                            version: ARTIFACT_VERSION,
                            repository: RELEASE_REPO,
                            credentialsId: NEXUS_JENKINS_CREDENTIAL,
                            artifacts: [
                                [artifactId: pom.artifactId,
                                classifier: '',
                                file: artifactPath,
                                type: pom.packaging],
                                [artifactId: pom.artifactId,
                                classifier: '',
                                file: "pom.xml",
                                type: "pom"]
                            ]
                        );
                    } 
		            else {
                        error "*** File: ${artifactPath}, could not be found";
                    }
                }
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