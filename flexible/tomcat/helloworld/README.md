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

Set your project, the plugins in this example are configured to use this value from gcloud

* `gcloud config set project <YOUR_PROJECT_NAME>`

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

* In the `pom.xml`, update the [App Engine Maven Plugin](https://cloud.google.com/appengine/docs/standard/java/tools/maven-reference)
with your Google Cloud Project Id:

```
<plugin>
  <groupId>com.google.cloud.tools</groupId>
  <artifactId>appengine-maven-plugin</artifactId>
  <version>2.2.0</version>
  <configuration>
    <projectId>myProjectId</projectId>
    <version>GCLOUD_CONFIG</version>
  </configuration>
</plugin>
```
**Note:** `GCLOUD_CONFIG` is a special version for autogenerating an App Engine
version. Change this field to specify a specific version name.

    $ mvn package appengine:deploy

## Gradle
[Using Gradle and the App Engine Plugin](https://cloud.google.com/appengine/docs/flexible/java/using-gradle)
& [Gradle Tasks and Parameters](https://cloud.google.com/appengine/docs/flexible/java/gradle-reference)

### Running locally

    $ gradle tomcatRun

### Deploying

    $ gradle appengineDeploy
