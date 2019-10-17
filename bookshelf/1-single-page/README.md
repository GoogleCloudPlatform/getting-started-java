# Bookshelf App for Java on App Engine Standard Tutorial
## Logging

Contains the code for using Cloud Firestore.

This is part of a [Bookshelf tutorial](https://cloud.google.com/java/getting-started).

Most users can get this running by updating the parameters in `pom.xml`. You'll
also need to [create a bucket][create-bucket] in Google Cloud Storage, referred
to below as `MY-BUCKET`.

[create-bucket]: https://cloud.google.com/storage/docs/creating-buckets

### Running Locally

To run locally, update the parameters in `pom.xml`:

* Replace `bookshelf.bucket` with the bucket created above.
* Replace `app.deploy.projectId` with your project ID.

Then run:

    mvn clean jetty:run-exploded

**Note**: If you run into an error about `Invalid Credentials`, you may have to run:

    gcloud auth application-default login

### Deploying to Appengine

To deploy your app, update the parameters in `pom.xml` as above, then run:

    mvn clean package appengine:deploy
