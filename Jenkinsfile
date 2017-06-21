#!groovy

properties(
    [[$class: 'GithubProjectProperty', displayName: 'java-logging', projectUrlStr: 'http://git.reform.hmcts.net/reform/java-logging/'],
    parameters(
        [string(name: 'slackChannel', description: 'Which Slack channel to send notifications to', defaultValue: '#devops')]
    ),
     pipelineTriggers([
             [$class: 'GitHubPushTrigger'],
             [$class: 'hudson.triggers.TimerTrigger', spec  : 'H 1 * * *']
     ])]
)

node {
    try {
        configure(env)
        stage('Checkout') {
            checkout scm
        }

        stage('Build') {
            sh '''
                ./gradlew clean build -x test
            '''
        }

        stage('Test') {
            sh '''
                ./gradlew test
            '''
        }

        if ("master" == "${env.BRANCH_NAME}") {
            stage('Install') {
                sh '''
                    ./gradlew install
                '''
            }
        }
    } catch (err) {
        if (getBinding().hasVariable('slackChannel')) {
            slackSend(
                    channel: slackChannel,
                    color: 'danger',
                    message: "${env.JOB_NAME}:  <${env.BUILD_URL}console|Build ${env.BUILD_DISPLAY_NAME}> has FAILED")
        }
        throw err
    }
}

private void configure(env) {
    env.JAVA_OPTS = "${env.JAVA_OPTS != null ? env.JAVA_OPTS : ''} ${proxySystemProperties(env)}"
}

private String proxySystemProperties(env) {
    def systemProperties = []

    if (env.http_proxy != null) {
        def proxyURL = new URL(env.http_proxy)
        systemProperties.add("-Dhttp.proxyHost=${proxyURL.getHost()}")
        systemProperties.add("-Dhttp.proxyPort=${proxyURL.getPort()}")
    }

    if (env.https_proxy != null) {
        def proxyURL = new URL(env.https_proxy)
        systemProperties.add("-Dhttps.proxyHost=${proxyURL.getHost()}")
        systemProperties.add("-Dhttps.proxyPort=${proxyURL.getPort()}")
    }

    if (env.no_proxy != null) {
        systemProperties.add("-Dhttp.nonProxyHosts=${env.no_proxy}")
    }

    return systemProperties.join(' ')
}
