# Java Server Pages based Hello World app

## Requirements
* [Apache Maven](http://maven.apache.org) 3.1 or greater
* JDK 8 in order to run
* [Cloud SDK for Managed VMs](https://cloud.google.com/appengine/docs/managed-vms/)


## Run the application

1. Set the correct Cloud SDK project via `gcloud config set project YOUR_PROJECT`
id of your application.
2. Run `mvn jetty:run-exploded`
4. Visit http://localhost:8080

## Deploy to AppEngine Managed VMs

5. `mvn gcloud:deploy`
6. Visit `http://YOUR_PROJECT.appspot.com`.
