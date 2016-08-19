# Servlet based Hello World app

## Requirements
* [Apache Maven](http://maven.apache.org) 3.1 or greater
* JDK 8 in order to run
* [Cloud SDK for Managed VMs](https://cloud.google.com/appengine/docs/managed-vms/)

## Setup

Use either:

* `gcloud init`
* `gcloud beta auth application-default login`

We support building with [Maven](), [Gradle](https://gradle.org), [Intelij Idea](https://cloud.google.com/tools/intellij/docs/), or [Eclipse]().
The samples have files to support both Maven and Gradle.  To use the IDE plugins, see the documentation pages above.

## Maven
### Running locally

    $ mvn jetty:run-exploded
  
### Deploying

    $ mvn appengine:deploy

## Gradle
### Running locally

    $ gradle jettyRun

### Deploying

    $ gradle appengineDeploy

