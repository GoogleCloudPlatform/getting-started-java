# Getting Started with Java - Google Compute Engine

See the [Bookshelf tutorial][tutorial] for help getting started with Google App Engine (Standard), Google Cloud
Firestore, and more.

You'll need to [create a bucket][create-bucket] in Google Cloud Storage,
referred to below as `MY-BUCKET`. You'll also need to create an OAuth2 client
and secret, and edit `pom.xml` with its values.

### Running Locally

    mvn clean jetty:run-war -DprojectID=YOUR-PROJECT-ID

### Deploying to Compute Engine

* Initialize the [Google Cloud SDK][cloud_sdk]

        gcloud init

* In the `makeProject` script update the `BUCKET` environment variable
  with your bucket name.

* Deploy your App

        ./makeProject gce

* To tear down the App, use

        ./makeProject down
        
### Deploying to Compute Engine with horizontal scaling

* Initialize Google Cloud SDK and `makeProject` as above.

* Deploy your App

        ./makeProject gce-many

* To tear down the App, use

        ./makeProject down-many

[tutorial]: https://cloud.google.com/java/getting-started/tutorial-app
[create-bucket]: https://cloud.google.com/storage/docs/creating-buckets#storage-create-bucket-console
[cloud_sdk]: https://cloud.google.com/sdk/
