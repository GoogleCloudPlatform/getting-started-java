# Servlet based Hello World app for App Engine Flexible environment

## Requirements
* [Apache Maven](http://maven.apache.org) (3.3.9 or greater) OR [Gradle ](https://gradle.org/) (4.2.1 or greater)
* [Google Cloud SDK](https://cloud.google.com/sdk/)
* `gcloud components install app-engine-java`
* `gcloud components update`

## Setup

Use either:

* `gcloud init`
* `gcloud auth application-default login`

We support building with [Maven](http://maven.apache.org/), [Gradle](https://gradle.org), [IntelliJ IDEA](https://cloud.google.com/tools/intellij/docs/), and [Eclipse](https://cloud.google.com/eclipse/docs/).  
The samples have files to support both Maven and Gradle.  To use the IDE plugins, see the documentation pages above.

## Maven
[Using Maven and the App Engine Plugin](https://cloud.google.com/appengine/docs/flexible/java/using-maven)
& [Maven Plugin Goals and Parameters](https://cloud.google.com/appengine/docs/flexible/java/maven-reference)

### Building

    $ mvn package

### Running locally

    $ mvn cargo:run
  
### Deploying

    $ mvn appengine:deploy

## Gradle
[Using Gradle and the App Engine Plugin](https://cloud.google.com/appengine/docs/flexible/java/using-gradle) 
& [Gradle Tasks and Parameters](https://cloud.google.com/appengine/docs/flexible/java/gradle-reference)

### Running locally

    $ gradle tomcatRun

### Deploying

    $ gradle appengineDeploy

