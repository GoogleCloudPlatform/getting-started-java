# Spring Boot based Hello World app

## Requirements
* [Apache Maven](http://maven.apache.org) 3.1 or greater
* JDK 8 in order to run
* [Cloud SDK for Managed VMs](https://cloud.google.com/appengine/docs/managed-vms/)

## Run the Application locally
1. Set the correct Cloud SDK project via `gcloud config set project YOUR_PROJECT`
id of your application.
2. Run `mvn spring-boot:run`
4. Visit http://localhost:8080

## Deploy to AppEngine Flexible Environment

5. `mvn gcloud:deploy`
6. Visit `http://YOUR_PROJECT.appspot.com`.
