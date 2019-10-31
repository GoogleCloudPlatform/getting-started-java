# Bookshelf App for Java on Cloud Run Tutorial

Contains the code for using Cloud Firestore.

This is part of a [Bookshelf tutorial](https://cloud.google.com/java/getting-started).

Most users can get this running by updating the parameters in `pom.xml`. You'll
also need to [create a bucket][create-bucket] in Google Cloud Storage, referred
to below as `MY_BUCKET`.

[create-bucket]: https://cloud.google.com/storage/docs/creating-buckets

### Running Locally

To run your project locally:

* Set the `BOOKSHELF_BUCKET` environment variable:

      export BOOKSHELF_BUCKET=<YOUR_BUCKET_NAME>
    
  Where <YOUR_BUCKET_NAME> is the bucket you created above.
* Run with the Jetty Maven plugin:

      mvn jetty:run-exploded 

**Note**: If you run into an error about `Invalid Credentials`, you may have to run:

    gcloud auth application-default login

### Deploying to Cloud Run

To build your image:

* Update the parameters in `pom.xml`:
  * Replace `MY_PROJECT` with your project ID.
* Build and deploy to your GCR with [Jib][jib] Maven plugin.

      mvn clean package jib:build
* Deploy the app to Cloud Run:

      cloud beta run deploy bookshelf --image gcr.io/<MY_PROJECT>/bookshelf \
            --platform managed --region us-central1 --memory 512M \
            --update-env-vars BOOKSHELF_BUCKET="<MY_BUCKET>"

Where <MY_PROJECT> is the name of the project you created.

This command will output a link to visit the page.

[jib]: https://github.com/GoogleContainerTools/jib
[configure-memory]: https://cloud.google.com/run/docs/configuring/memory-limits
