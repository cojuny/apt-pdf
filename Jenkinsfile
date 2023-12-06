def COLOR_MAP = [
    'SUCCESS': 'good', 
    'FAILURE': 'danger',
]

pipeline {
    agent any

    environment {
        def REPO = "cojuny/apt-pdf"
    }

    tools {
        maven "maven3"
        jdk "jdk11"
    }

    stages {
        
        stage('Python-Build') {
            steps {
                sh '''
                    python -m venv jenkins_venv
                    source jenkins_venv/bin/activate
                    pip install -r SearchEngine/requirements.txt
                    '''
            }
        }

        stage ('Python-Test') {
            steps {
                sh '''
                    source jenkins_venv/bin/activate
                    pytest --junitxml=target/python-reports/xunit.xml SearchEngine
                    pytest --cov-report xml:target/python-reports/coverage.xml --cov=SearchEngine/src SearchEngine                    '''
            }
        }

        stage('Java-Build') {
            steps {
                sh 'mvn install -DskipTests'
            }

            post {
                success {
                    echo 'Archiving artifacts...'
                    archiveArtifacts artifacts: '**/*.jar'
                }
            }
        }

        stage('Java-Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Java-CheckstyleAnalysis') {
            steps {
                sh 'mvn checkstyle:checkstyle'
            }
        }
    
    
        stage('Sonar Analysis') {
            environment {
                scannerHome = tool 'SonarQube Scanner 4.7.0'
            }
            steps {
                withSonarQubeEnv('sonarqube_server') {
                    sh '''${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=apt-pdf \
                    -Dsonar.projectName=apt-pdf \
                    -Dsonar.projectVersion=1.0 \
                    -Dsonar.sources=src/,SearchEngine/src/ \
                    -Dsonar.java.binaries=target/test-classes/com/searcher/ \
                    -Dsonar.junit.reportsPath=target/surefire-reports/ \
                    -Dsonar.java.checkstyle.reportPaths=target/checkstyle-result.xml \
                    -Dsonar.python.xunit.reportPath=target/python-reports/xunit.xml \
                    -Dsonar.python.coverage.reportPath=target/python-reports/coverage.xml \
                '''
                }
            }
        }

        
        
        /*
        stage('Github Release') {
            when {
                expression { currentBuild.resultIsBetterOrEqualTo('SUCCESS') }
            }
            steps {
                withCredentials([string(credentialsId: 'github_token', variable: 'TOKEN')]) {
                    sh '''#!/bin/bash
                        LAST_LOG=$(git log --format='%H' --max-count=1 origin/release)
                        echo "LAST_LOG:$LAST_LOG"
                        LAST_MERGE=$(git log --format='%H' --merges --max-count=1 origin/release)
                        echo "LAST_MERGE:$LAST_MERGE"
                        LAST_MSG=$(git log --format='%s' --max-count=1 origin/release)
                        echo "LAST_MSG:$LAST_MSG"
                        VERSION=$(echo $LAST_MSG | grep --only-matching 'v\\?[0-9]\\+\\.[0-9]\\+\\(\\.[0-9]\\+\\)\\?')
                        echo "VERSION:$VERSION"
                        
                        if [[ $LAST_LOG == $LAST_MERGE && -n $VERSION ]]
                        then
                            echo "Creating tag to the last commit..."
                            DATA='{
                                "tag": "'$VERSION'"
                                "message": "Release Version: $VERSION"
                                "object": "'$LAST_LOG'"
                                "type": "commit"
                            }'
                            curl -H "Authorization: token $TOKEN" --data "$DATA" "https://api.github.com/repos/$REPO/git/tags"


                            echo "Creating release $VERSION based on tag..."
                            DATA='{
                                "tag_name": "'$VERSION'",
                                "name": "'$VERSION'",
                                "body": "'$LAST_MSG'",
                                "draft": false,
                                "prerelease": false
                            }'
                            curl -H "Authorization: token $TOKEN" --data "$DATA" "https://api.github.com/repos/$REPO/releases"
                        else
                            echo "No merge action detected, skipping..."
                        fi
                    '''
                }
            
            }
        }
        */
    
    }
    
    post {
        always {
            echo 'Slack Notifications.'
            slackSend channel: '#jenkinscicd',
                color: COLOR_MAP[currentBuild.currentResult],
                message: "*${currentBuild.currentResult}:* Job ${env.JOB_NAME} build ${env.BUILD_NUMBER} \n More info at: ${env.BUILD_URL}"
        }
    }
}