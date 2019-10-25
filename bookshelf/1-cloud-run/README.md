# Bookshelf App for Java on Cloud Run Tutorial

Contains the code for using Cloud Firestore.

This is part of a [Bookshelf tutorial](https://cloud.google.com/java/getting-started).

Most users can get this running by updating the parameters in `pom.xml`. You'll
also need to [create a bucket][create-bucket] in Google Cloud Storage, referred
to below as `MY_BUCKET`.

[create-bucket]: https://cloud.google.com/storage/docs/creating-buckets

### Running Locally

To run locally, update the parameters in `pom.xml`:

* Replace `MY_PROJECT` with your project ID.
* Replace `MY_BUCKET` with the bucket created above.

Then run:

    mvn clean jetty:run-exploded

**Note**: If you run into an error about `Invalid Credentials`, you may have to run:

    gcloud auth application-default login

### Deploying to Cloud Run

To build your image, update the parameters in `pom.xml` as above, then run:

    mvn clean package jib:build
    
Increase the [memory limit ][configure-memory]for the service:

    gcloud beta run services update bookshelf --memory 512M

When the build is successful, deploy the app to Cloud Run:

    gcloud beta run deploy bookshelf --image gcr.io/<MY_PROJECT>/bookshelf \
    --platform managed --region us-central1

This command will output a link to visit the page.

[configure-memory]: https://cloud.google.com/run/docs/configuring/memory-limits
