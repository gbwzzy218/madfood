podTemplate(label: 'mypod', containers: [
  // containerTemplate(name: 'docker', image: 'docker', command: 'cat', ttyEnabled: true),
  containerTemplate(name: 'kubectl', image: 'lachlanevenson/k8s-kubectl:v1.8.8', command: 'cat', ttyEnabled: true),
  // containerTemplate(name: 'helm', image: 'lachlanevenson/k8s-helm:latest', command: 'cat', ttyEnabled: true)
],
volumes: [
    hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock'),
]) 
{
    node('mypod') {
            stage('Check running containers') {
            container('kubectl') {
                // example to show you can run docker commands when you mount the socket
                sh 'hostname'
                sh 'hostname -i'
                sh 'whoami'
                sh 'hostname -i'
                sh 'pwd'
                sh 'ls'
            }
        }
        
        stage('Build') {
                sh 'whoami'
                sh 'hostname -i'
                sh 'pwd'
                sh 'ls'
        }

    }
}
