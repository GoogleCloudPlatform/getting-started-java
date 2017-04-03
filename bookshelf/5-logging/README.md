# Bookshelf App for Java Tutorial
## Logging

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

* Deploy your App

    mvn clean appengine:deploy \
        -Dbookshelf.bucket=MY-BUCKET


