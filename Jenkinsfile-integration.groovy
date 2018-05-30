def randomString() {
    def rand = new Random()
    def generator = { String alphabet, int n ->
        new Random().with {
            (1..n).collect { alphabet[ rand.nextInt( alphabet.length() ) ] }.join('')
        }
    }
    return generator( (('a'..'z')+('A'..'Z')+('0'..'9')).join(''), 9 )
}

// TODO: write help secreteNamesMap if there should be aprefix specify it if not empty string
def OCDeployApp (def clusterName, def projectName, def appName, def imageName, def imageTag, def serviceName, Map<String,String> envVarsMap, Map<String,String> configMapsMap, Map<String,String> secreteNamesMap, def minutesToWaitForContainerReady, boolean exposeService){
    openshift.withCluster(clusterName) {
        // openshift.verbose() - if needed verbose is output is possible.
        openshift.withProject(projectName) {
            def dcs = openshift.newApp("${imageName}:${imageTag} --name ${serviceName}").narrow('dc')

            // Tag the service
            def svc = openshift.selector("svc/${serviceName}").object()
            svc.metadata.labels['servicename']=appName
            svc.metadata.labels['version']=imageTag
            openshift.apply(svc)

            // Set required env variables for the app
            envVarsMap.each{ openshift.set("env", "dc/${serviceName} ${it.key}=${it.value}") }
            secreteNamesMap.each {
                String cmd = "--from=secret/${it.key}"
                if (it.value != null && !it.value.isEmpty()) {
                    cmd += " --prefix=${it.value}"
                }
                openshift.set("env", "dc/${serviceName}", cmd)
            }

            // Set required config maps for the app
            configMapsMap.each {
                String cmd = "--from=configmap/${it.key}"
                if (it.value != null && !it.value.isEmpty()) {
                    cmd += " --prefix=${it.value}"
                }
                openshift.set("env", "dc/${serviceName}", cmd)
            }

            // Start the app and wait for container to be ready
            try {
                timeout(minutesToWaitForContainerReady.toInteger()) {
                    dcs.related('pods').untilEach(1) {
                        return (it.object().status.containerStatuses[0].ready)
                    }
                }
            }
            catch (InterruptedException err) {
                // In case the container failed to start, fail the build.
                echo failureString("The created container status is not in 'ready' after defined timeout: ${minutesToWaitForContainerReady} minutes, failing the build.")
                error("The created container status is not in 'ready' after defined timeout: ${minutesToWaitForContainerReady} minutes, failing the build.")
            }

            if (exposeService) {
                openshift.raw("expose service ${serviceName}", "--name ${serviceName}")
            }
        }
    }
}

def createSecret(def clusterName, def projectName, def secretName, Map<String,String> secretVars) {
    openshift.withCluster(clusterName) {
        openshift.withProject(projectName) {
            try {
                // Try and get the secret (might already exist)
                openshift.raw("get secret ${secretName}")
            } catch (Exception e) {
                // In case we cant get the secret, create it.
                String literals = ""
                secretVars.each { literals += " --from-literal=${it.key}=${it.value}" }
                openshift.raw("create secret generic ${secretName}", literals)
            }
        }
    }
}

String infoString(String message) {
    return "\u001B[38;5;21m${message}\u001B[0m"
}

String successString(String message) {
    return "\u001B[1;38;5;34m${message}\u001B[0m"
}

String failureString(String message) {
    return "\u001B[1;38;5;124m${message}\u001B[0m"
}

def server = Artifactory.server "ArtifactoryHA"
def rtMaven = Artifactory.newMavenBuild()
def buildInfo

pipeline {
    agent { label "docker" }
    environment {
        ocClusterName = "oc_dev"

        appName = "account-service"
        ocProjectName = "${appName}-test"
        dockerRepo = "myrepo"
        minutesToWaitForContainerReady = 2

        // App
        imageName = "${dockerRepo}/${appName}"
        imageTag = "${BUILD_NUMBER}"
        ocServiceName = "${appName}-${imageTag}"

        // DB
        dbImageName = "mysql"
        dbImageTag = "5.7.21"
        dbAppName = "${appName}-db"
        ocDBServiceName = "${dbAppName}-${imageTag}"
        dbSecretName = "${dbAppName}-secret"
    }
    options {
        ansiColor("xterm")
        timestamps()
        timeout(time: 1, unit: "HOURS")
    }

    stages {
        stage("maven build") {
            agent {
                docker {
                    image "maven:3-alpine"
                    label "docker"
                    args '-v $HOME/.m2:/root/.m2 -v /tmp:/app'
                }
            }
            steps {
                script {
                    rtMaven.tool = "Maven Docker"
                    rtMaven.deployer releaseRepo: "release-artifacts", snapshotRepo: "snapshots-artifacts", server: server
                    rtMaven.resolver releaseRepo: "virtual-repo", snapshotRepo: "virtual-repo", server: server
                    buildInfo = Artifactory.newBuildInfo()
                    rtMaven.run pom: "pom.xml", goals: "clean install -V -B -U -e -DskipTests", buildInfo: buildInfo
                    server.publishBuildInfo buildInfo
                }
            }
        }
        stage("build docker image") {
            agent { label "docker" }
            steps {
                script {
                    pom = readMavenPom file: "pom.xml"
                    jar_file = "target/" + pom.artifactId + "-" + pom.version + ".jar"
                    docker.withRegistry("https://${dockerRepo}", "ArtifactoryUser") {
                        def dockerImage = docker.build("${imageName}:${imageTag}", "--build-arg JAR_PATH=${jar_file} .")
                        dockerImage.push()
                        dockerImage.push("latest")
                    }
                }
            }
        }
        stage("deploy full env") {
            agent { node "master" }
            steps {
                script {
                    echo infoString("Deploy full env")
                    def appEnvVarsMap = ["DATABASE_SERVER": ocDBServiceName,
                                         "DATABASE_NAME"  : "account-service",
                                         "DATABASE_PORT"  : "3306"
                    ]
                    def appSecreteNamesMap = ["${dbSecretName}": "DATABASE_"]

                    def dbEnvVarsMap = ["MYSQL_DATABASE": appName,
                                        "MYSQL_ROOT_PASSWORD": randomString()]
                    def dbSecreteNamesMap = ["${dbSecretName}": "MYSQL_"]
                    def dbSecret = ["user"    : randomString(),
                                    "password": randomString()
                    ]

                    // Deploy DB
                    createSecret(ocClusterName, ocProjectName, dbSecretName, dbSecret)
                    OCDeployApp(ocClusterName, ocProjectName, dbAppName, dbImageName, dbImageTag, ocDBServiceName, dbEnvVarsMap, null, dbSecreteNamesMap, minutesToWaitForContainerReady, false)

                    // Deploy App
                    OCDeployApp(ocClusterName, ocProjectName, appName, imageName, imageTag, ocServiceName, appEnvVarsMap, null, appSecreteNamesMap, minutesToWaitForContainerReady, true)
                }
            }
        }
    }
    post {
        always {
            echo "Done"
        }
        success {
            echo "Success"
        }
        failure {
            echo "Failure"
        }
    }
}