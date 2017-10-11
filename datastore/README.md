# Google App Engine Standard Environment Datastore Sample

This sample demonstrates how to deploy an App Engine Java 8 application that uses Cloud Datastore for storage.

* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Maven](https://maven.apache.org/download.cgi) (at least 3.3.9)
* [Google Cloud SDK](https://cloud.google.com/sdk/) (aka gcloud)

Initialize the Google Cloud SDK using:

    gcloud init

    gcloud auth application-default login

## Setup

1.  Update the `<appId>` tag in the `pom.xml` with your application ID.

## Using Maven

### Run Locally

    mvn appengine:devserver

### Deploy to App Engine Standard for Java 8

    mvn appengine:update

See the [Google App Engine standard environment documentation][ae-docs] for more detailed instructions.

[ae-docs]: https://cloud.google.com/appengine/docs/java/
