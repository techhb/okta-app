pipeline {
   agent any

   environment {
       // use your actual issuer URL here and NOT the placeholder {yourOktaDomain}
       OKTA_OAUTH2_ISSUER           = 'https://dev-658088.okta.com/oauth2/default'
       OKTA_OAUTH2_CLIENT_ID        = credentials('OKTA_OAUTH2_CLIENT_ID')
       OKTA_OAUTH2_CLIENT_SECRET    = credentials('OKTA_OAUTH2_CLIENT_SECRET')

   }

   stages {
   stage('Init') {
   steps {
   // Get some code from a GitHub repository
    git 'https://github.com/techhb/okta-app.git'
      }
   }
      stage('Build') {
         steps {


            // Run Maven on a Unix agent.
          //  sh "./mvnw -Dmaven.test.failure.ignore=true clean package"

            // To run Maven on a Windows agent, use
            // bat "mvn -Dmaven.test.failure.ignore=true clean package"

            sh '''
         ./mvnw -Dmaven.test.failure.ignore=true clean package
         cd target
         cp ../src/main/resources/web.config web.config
         cp oktaapp-0.0.1-SNAPSHOT.jar app.jar
         rm -rf oktaapp-0.0.1-SNAPSHOT.jar
         rm -rf /var/jenkins_home/workspace/okta-app/*.zip
      '''
        script{
        zip  archive: true,dir: './target', glob: 'app.jar,web.config', zipFile: 'oktaapp.zip'

        }
         }

        // post {
            // If Maven was able to run the tests, even if some of the test
            // failed, record the test results and archive the jar file.
          //  success {
            //   junit '**/target/surefire-reports/TEST-*.xml'
              // archiveArtifacts 'target/*.jar'
            //}

        // }

      }
      stage('Deploy') {
      steps {
     azureWebAppPublish azureCredentialsId: env.AZURE_CRED_ID,resourceGroup: env.RES_GROUP, appName: env.WEB_APP, filePath: "**/oktaapp.zip"
     }
  }
      post {
  always {
    cleanWs()
    }

   }
}
}
