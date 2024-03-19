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
                    pip install --upgrade pip
                    pip install -r SearchEngine/requirements.txt
                    '''
            }
        }

        stage ('Python-Test') {
            steps {
                sh '''
                    mkdir -p target/python-reports
                    source jenkins_venv/bin/activate
                    pytest --junitxml=target/python-reports/xunit.xml SearchEngine
                    pytest --cov-report xml:target/python-reports/coverage.xml --cov=SearchEngine/src SearchEngine
                    pwd
                    ls target
                    '''
            }
        }

        stage('Java-Build') {
            steps {
                sh 'mvn install -DskipTests'
                sh '''
                pwd
                ls target
                '''
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
                sh 'mvn clean compile test'
                sh '''
                pwd
                ls target
                '''
            }
        }

        stage('Java-CheckstyleAnalysis') {
            steps {
                sh 'mvn checkstyle:checkstyle'
                sh '''
                pwd
                ls target
                '''
            }
        }
    
    
        stage('Sonar Analysis') {
            environment {
                scannerHome = tool 'SonarQube Scanner 4.7.0'
            }
            steps {
                withSonarQubeEnv('sonarqube_server') {
                    sh '''
                    pwd
                    ls target -a
                    ${scannerHome}/bin/sonar-scanner \
                    -Dsonar.projectKey=apt-pdf \
                    -Dsonar.projectName=apt-pdf \
                    -Dsonar.projectVersion=1.0 \
                    -Dsonar.sources=src/,SearchEngine/src/ \
                    -Dsonar.java.binaries=target/test-classes/com/searcher/ \
                    -Dsonar.junit.reportsPath=target/surefire-reports/ \
                    -Dsonar.jacoco.reportsPath=target/jacoco.exec \
                    -Dsonar.java.checkstyle.reportPaths=target/checkstyle-result.xml \
                    -Dsonar.python.version=3.x \
                    -Dsonar.python.xunit.reportPaths=target/python-reports/xunit.xml \
                    -Dsonar.python.coverage.reportPaths=target/python-reports/coverage.xml
                '''
                }
            }
        }
    
    }
    
    post {
        always {
            echo 'Slack Notifications.'
            slackSend channel: '#apt-pdf',
                color: COLOR_MAP[currentBuild.currentResult],
                message: "*${currentBuild.currentResult}:* Job ${env.JOB_NAME} build ${env.BUILD_NUMBER} \n More info at: ${env.BUILD_URL}"
        }
    }
}