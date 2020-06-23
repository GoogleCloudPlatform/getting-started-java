# Bookshelf App for Java Tutorial
## Auth

Contains the code for using Cloud Datastore and Cloud SQL v2.

This is part of a [Bookshelf tutorial][tutorial].

You'll need to [create a bucket][create-bucket] in Google Cloud Storage,
referred to below as `MY-BUCKET`. You'll also need to create an OAuth2 client
and secret, and edit `pom.xml` with its values. See the [tutorial][tutorial] for
details.

[tutorial]: https://cloud.google.com/java/getting-started/tutorial-app

### Running Locally

      mvn clean jetty:run-exploded \
          -Dbookshelf.bucket=MY-BUCKET


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

      mvn clean package appengine:deploy \
          -Dbookshelf.bucket=MY-BUCKET
