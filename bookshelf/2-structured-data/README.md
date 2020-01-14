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

* Update the parameters in `pom.xml`:
  * Replace `myProjectId` with your project ID.

* Deploy your App

      mvn clean package appengine:deploy

