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
        NEXUS_PROTOCOL = "https"
        NEXUS_URL = "nexus.duongdx.com"// url

        GROUP_ID = "io.john.programming"       // Replace with your actual groupId in `pom.xml`
        ARTIFACT_ID = "todo-app"       // Replace with your actual artifactId in `pom.xml`
        PACKAGING = "jar"               // Assuming your packaging in `pom.xml`
        ARTIFACT_TYPE = "SNAPSHOT"
        ARTIFACT_VERSION = "${env.BUILD_ID}-${ARTIFACT_TYPE}"

        SONAR_SERVER = "duongdx_sonarqube_server"
        SONAR_SCANNER = "sonarscanner6"
    }

    triggers {
        GenericTrigger(
            genericVariables: [
                // [key: 'payload', value: '$'], // Extract all variable from payload
                [key: 'pr_action', value: '$.action'], // Extract action from payload
                [key: 'pr_number', value: '$.pull_request.number'], // Extract PR number
                [key: 'pr_head_branch', value: '$.pull_request.head.ref'], // Extract source branch
                [key: 'pr_base_branch', value: '$.pull_request.base.ref'], // Extract target branch
                [key: 'pr_commit_sha', value: '$.pull_request.head.sha'], // Extract commit SHA
                [key: 'repo_name', value: '$.repository.full_name'], // Extract repository name
                [key: 'repo_url', value: '$.repository.html_url'] // Extract repo URL
            ],
            genericHeaderVariables: [
                [key: 'github_event', expressionType: 'header', value: 'X-GitHub-Event'], // Extract event type
                [key: 'github_delivery', expressionType: 'header', value: 'X-GitHub-Delivery'] // Extract unique delivery ID
            ],
            causeString: 'Triggered on $github_event',
            token: 'secret_token', // Correct usage of the token here
            printContributedVariables: true, // These flags are set to true to ensure that you see all available variables in the Jenkins console.
            printPostContent: true, // These flags are set to true to ensure that you see all available variables in the Jenkins console.
            regexpFilterExpression: '',
            regexpFilterText: ''
        )
    }

    stages {
        stage('Verify trigger variable') {
            steps{
                // Output captured values
                // echo "All: ${payload}"
                echo "GitHub Event: ${github_event}"
                echo "Pull Request Action: ${pr_action}"
                echo "Pull Request Number: ${pr_number}"
                echo "Repository Name: ${repo_name}"
                echo "Head Branch: ${pr_head_branch}"
                echo "Base Branch: ${pr_base_branch}"
                echo "Commit SHA: ${pr_commit_sha}"
            }
        }

        stage('BUILD') {
            steps {
                withCredentials([usernamePassword(credentialsId: NEXUS_JENKINS_CREDENTIAL, usernameVariable: 'NEXUS_USERNAME', passwordVariable: 'NEXUS_PASSWORD')]) {
                    sh 'env'

                    sh 'mvn -s settings.xml clean install -DskipTests'
                }
            }
            post {
                success {
                    echo 'Now Archiving...'
                    archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
                    stash name: 'app-jar', includes: '**/target/*.jar' // save the artifact for Deploy State
                }
            }
        }

        stage('UNIT TEST') {
            steps {
                withCredentials([usernamePassword(credentialsId: NEXUS_JENKINS_CREDENTIAL, usernameVariable: 'NEXUS_USERNAME', passwordVariable: 'NEXUS_PASSWORD')]) {
                    sh 'mvn -s settings.xml test'
                }
            }
        }

        stage('INTEGRATION TEST') {
            steps {
                withCredentials([usernamePassword(credentialsId: NEXUS_JENKINS_CREDENTIAL, usernameVariable: 'NEXUS_USERNAME', passwordVariable: 'NEXUS_PASSWORD')]) {
                    sh 'mvn -s settings.xml verify -DskipUnitTests'
                }
            }
        }
		
        stage ('CODE ANALYSIS WITH CHECKSTYLE') {
            steps {
                withCredentials([usernamePassword(credentialsId: NEXUS_JENKINS_CREDENTIAL, usernameVariable: 'NEXUS_USERNAME', passwordVariable: 'NEXUS_PASSWORD')]) {
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
            // target/test-classes/io/john/programming/todoapp: is testing code
            steps {
                withSonarQubeEnv("${SONAR_SERVER}") {
                    sh '''${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=spring-boot-todo-list \
                        -Dsonar.projectName=spring-boot-todo-list \
                        -Dsonar.projectVersion=1.0 \
                        -Dsonar.sources=src/ \
                        -Dsonar.java.binaries=target/classes,target/test-classes/io/john/programming/todoapp \
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

        // stage("Publish to Nexus Repository Manager") {
        //     steps {
        //         script {
        //             // Locate the built artifact
        //             def filesByGlob = findFiles(glob: "target/*.${PACKAGING}")
        //             def repoName = ARTIFACT_TYPE == "RELEASE" ? RELEASE_REPO : SNAPSHOT_REPO

        //             if (filesByGlob && filesByGlob.size() > 0) {
        //                 def artifactPath = filesByGlob[0].path
        //                 def artifactExists = fileExists(artifactPath)

        //                 if (artifactExists) {
        //                     echo "*** File: ${artifactPath}, group: ${GROUP_ID}, packaging: ${PACKAGING}, repository: ${repoName}, version: ${ARTIFACT_VERSION}"
        //                     nexusArtifactUploader(
        //                         nexusVersion: NEXUS_VERSION,
        //                         protocol: NEXUS_PROTOCOL,
        //                         nexusUrl: NEXUS_URL,
        //                         groupId: GROUP_ID,
        //                         version: ARTIFACT_VERSION,
        //                         repository: repoName,
        //                         credentialsId: "${NEXUS_JENKINS_CREDENTIAL}",
        //                         artifacts: [
        //                             [
        //                                 artifactId: ARTIFACT_ID,
        //                                 classifier: '',
        //                                 file: artifactPath,
        //                                 type: PACKAGING
        //                             ],
        //                             [
        //                                 artifactId: ARTIFACT_ID,
        //                                 classifier: '',
        //                                 file: "pom.xml",
        //                                 type: "pom"
        //                             ]
        //                         ]
        //                     )
        //                 } else {
        //                     error "*** File: ${artifactPath}, could not be found"
        //                 }
        //             } else {
        //                 error "*** No files matching the glob pattern were found."
        //             }
        //         }
        //     }
        // }

        stage('Deploy to Nexus') {
            steps {
                script {
                    def filesByGlob = findFiles(glob: "target/*.${PACKAGING}")
                    def repoName = ARTIFACT_TYPE == "RELEASE" ? RELEASE_REPO : SNAPSHOT_REPO

                    if (filesByGlob && filesByGlob.size() > 0) {
                        def artifactPath = filesByGlob[0].path
                        def artifactExists = fileExists(artifactPath)
                        withCredentials([usernamePassword(credentialsId: NEXUS_JENKINS_CREDENTIAL, usernameVariable: 'NEXUS_USERNAME', passwordVariable: 'NEXUS_PASSWORD')]) {
                            echo "*** File: ${artifactPath}, group: ${GROUP_ID}, packaging: ${PACKAGING}, repository: ${repoName}, version: ${ARTIFACT_VERSION}"
                            sh """
                                mvn -s settings.xml deploy:deploy-file \
                                -DgroupId=${GROUP_ID} \
                                -DartifactId=${ARTIFACT_ID} \
                                -Dversion=${ARTIFACT_VERSION} \
                                -Dpackaging=${PACKAGING} \
                                -Dfile=${artifactPath} \
                                -DpomFile=pom.xml \
                                -DrepositoryId=${repoName} \
                                -Durl=${NEXUS_DOMAIN}/repository/${repoName}
                            """
                        }
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