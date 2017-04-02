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

* Deploy your App

    mvn clean appengine:deploy

