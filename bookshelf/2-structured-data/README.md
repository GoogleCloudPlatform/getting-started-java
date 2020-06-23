# Bookshelf App for Java Tutorial
## Structured Data

Contains the code for using Cloud Datastore and Cloud SQL v2.

This is part of a [Bookshelf tutorial](https://cloud.google.com/java/getting-started/tutorial-app).

Most users can get this running by updating the parameters in `pom.xml`.

### Running Locally

    mvn clean jetty:run-exploded

### Deploying to App Engine Flexible

* Initialize the [Google Cloud SDK]()

      gcloud init

* In the `pom.xml`, update the [App Engine Maven Plugin](https://cloud.google.com/appengine/docs/standard/java/tools/maven-reference)
with your Google Cloud Project Id:

  ```
  <plugin>
    <groupId>com.google.cloud.tools</groupId>
    <artifactId>appengine-maven-plugin</artifactId>
    <version>2.3.0</version>
    <configuration>
      <projectId>myProjectId</projectId>
      <version>GCLOUD_CONFIG</version>
    </configuration>
  </plugin>
  ```
  **Note:** `GCLOUD_CONFIG` is a special version for autogenerating an App Engine
  version. Change this field to specify a specific version name.

* Deploy your App

      mvn clean package appengine:deploy
