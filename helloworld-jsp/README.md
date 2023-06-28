# Java Server Pages based Hello World app

## Requirements
* [Apache Maven](http://maven.apache.org) 3.3.9 or greater
* [Google Cloud SDK](https://cloud.google.com/sdk/)
* `gcloud components install app-engine-java`
* `gcloud components update`

## Setup

Use either:

* `gcloud init`
* `gcloud beta auth application-default login`

Set your project, the plugins in this example are configured to use this value from gcloud

* `gcloud config set project <YOUR_PROJECT_NAME>`

We support building with [Maven](http://maven.apache.org/), [Gradle](https://gradle.org), and [IntelliJ IDEA](https://cloud.google.com/tools/intellij/docs/).
The samples have files to support both Maven and Gradle.  To use the IDE plugins, see the documentation pages above.

## Maven
[Using Maven and the App Engine Plugin](https://cloud.google.com/appengine/docs/flexible/java/using-maven)
& [Maven Plugin Goals and Parameters](https://cloud.google.com/appengine/docs/flexible/java/maven-reference)
### Running locally

    $ mvn jetty:run-exploded

### Deploying

* In the `pom.xml`, update the [App Engine Maven Plugin](https://cloud.google.com/appengine/docs/standard/java/tools/maven-reference)
with your Google Cloud Project Id:

  ```
  <plugin>
    <groupId>com.google.cloud.tools</groupId>
    <artifactId>appengine-maven-plugin</artifactId>
    <version>2.3.0</version>
    <configuration>
      <projectId>GCLOUD_CONFIG</projectId>
      <version>GCLOUD_CONFIG</version>
    </configuration>
  </plugin>
  ```
  **Note:** `GCLOUD_CONFIG` is a special version for autogenerating an App Engine
  version. Change this field to specify a specific version name.

* Deploy your App  
  ```
    $ mvn package appengine:deploy
  ```

## Gradle
[Using Gradle and the App Engine Plugin](https://cloud.google.com/appengine/docs/flexible/java/using-gradle)
& [Gradle Tasks and Parameters](https://cloud.google.com/appengine/docs/flexible/java/gradle-reference)
### Running locally

    $ gradle jettyRun

### Deploying

    $ gradle appengineDeploy
