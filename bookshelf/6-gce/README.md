# Bookshelf App for Java Tutorial
## Google Compute Engine

Contains the code for using Cloud Datastore and Cloud SQL v2.

This is part of a [Bookshelf tutorial][tutorial].

You'll need to [create a bucket][create-bucket] in Google Cloud Storage,
referred to below as `MY-BUCKET`. You'll also need to create an OAuth2 client
and secret, and edit `pom.xml` with its values. See the [tutorial][tutorial] for
details.

### Running Locally

    mvn clean jetty:run-exploded \
        -DprojectID=YOUR-PROJECT-ID \
        -Dbookshelf.bucket=YOUR-BUCKET-NAME

### Deploying to App Engine Flexible

* Initialize the [Google Cloud SDK][cloud_sdk]

        gcloud init

* In the `pom.xml` update properties `projectID` and `bookshelf.bucket` with
  your project id and your bucket name respectively.

* In the `makeBookshelf` script update the `BUCKET` environment variable
  with your bucket name.

* Deploy your App

        ./makeBookshelf gce

[cloud_sdk]: https://cloud.google.com/sdk/
[tutorial]: https://cloud.google.com/java/getting-started/tutorial-app
[create-bucket]: https://cloud.google.com/storage/docs/creating-buckets#storage-create-bucket-console
