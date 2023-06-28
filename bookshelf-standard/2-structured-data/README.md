mvn package appengine:deploymvn package appengine:deploymvn package appengine:deploy# Bookshelf App for Java on App Engine Standard Tutorial
## Structured Data

Contains the code for using Cloud Datastore and Cloud SQL v2.

This is part of a [Bookshelf tutorial](https://cloud.google.com/java/getting-started/tutorial-app).

Most users can get this running by updating the parameters in `pom.xml`.

### Running Locally

    mvn -Plocal clean appengine:devserver

### Deploying to App Engine Standard

* In the `pom.xml`, update the [App Engine Maven Plugin](https://cloud.google.com/appengine/docs/standard/java/tools/maven-reference)
with your Google Cloud Project Id:

  ```
  <plugin>
    <groupId>com.google.cloud.tools</groupId>
    <artifactId>appengine-maven-plugin</artifactId>
    <version>2.3.0</version>
    <configuration>
      <projectId>GCLOUD_CONFIG</projectId>
      <version>bookshelf</version>
    </configuration>
  </plugin>
  ```

* Deploy your App

    mvn package appengine:deploy

Visit it at http://bookshelf.<your-project-id>.appspot.com
