# Bookshelf App for Java Tutorial
## Binary Data

Contains the code for using Cloud Datastore and Cloud SQL v2.

This is part of a [Bookshelf tutorial](https://cloud.google.com/java/getting-started/tutorial-app).

Most users can get this running by updating the parameters in `pom.xml`. You'll
also need to [create a bucket][create-bucket] in Google Cloud Storage, referred
to below as `MY-BUCKET`.

### Running Locally

    mvn clean jetty:run-exploded \
        -Dbookshelf.bucket=MY-BUCKET

### Deploying to App Engine Flexible

* Initialize the [Google Cloud SDK]()

    gcloud init

* Deploy your App

    mvn clean appengine:deploy \
        -Dbookshelf.bucket=MY-BUCKET

