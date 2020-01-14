HelloWorld for App Engine Standard (Java 8)
============================

This sample demonstrates how to launch a dataflow job from Google App Engine
Standard.

See the [Google App Engine standard environment documentation][ae-docs] for more
detailed instructions.

[ae-docs]: https://cloud.google.com/appengine/docs/java/


* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Maven](https://maven.apache.org/download.cgi) (at least 3.5)
* [Gradle](https://gradle.org/gradle-download/) (optional)
* [Google Cloud SDK](https://cloud.google.com/sdk/) (aka gcloud)
* [Google Cloud Dataflow](https://cloud.google.com/dataflow/docs/)
* [Google Cloud Dataflow Templates](https://cloud.google.com/dataflow/docs/templates/overview)

## Setup

1. Download and initialize the [Cloud SDK](https://cloud.google.com/sdk/)

    ```
    gcloud init
    ```

1. Create an App Engine app within the current Google Cloud Project

    ```
    gcloud app create
    ```

1. Enable the dataflow API

    ```
    gcloud services enable dataflow.googleapis.com
    ```

1. Set the project and bucket names (required for the following steps). These
   commands add generic names; feel free to adjust appropriately.

    ```
    export PROJECT=<your-project-name-here>
    export BUCKET=$PROJECT-template
    ```

1. Create the GCS bucket that dataflow will use

    ```
    gsutil mb gs://$BUCKET
    ```

1. Update the source to use your project and bucket names in the dataflow job.

    ```
    sed -i "s/YOUR_PROJECT_NAME/$PROJECT/" src/main/java/com/example/appengine/java8/LaunchDataflowTemplate.java
    sed -i "s/YOUR_BUCKET_NAME/$BUCKET/" src/main/java/com/example/appengine/java8/LaunchDataflowTemplate.java
    ```

## Maven
### Running locally

    mvn appengine:run

To use vist: http://localhost:8080/

### Deploying

    mvn appengine:deploy

To use, visit:  https://YOUR-PROJECT-ID.appspot.com

To delete the deployed version, see
https://cloud.google.com/java/getting-started/delete-tutorial-resources#deleting_app_versions.

Please note that deploying this project will incur charges at the F1 (default)
type, as described in the pricing schedule on
https://cloud.google.com/appengine/pricing#standard_instance_pricing. The
launched dataflow job will be charged as described in
https://cloud.google.com/dataflow/pricing, but will likely not incur any charges
so long as there are little to no entities in the target datastore instance.

*Note that as of 2018-02-05, gcloud 187.0.0 will throw
[errors](https://stackoverflow.com/a/48567678) when you try to
deploy. Version 186.0.0, however, should deploy successfully.*

## Testing

    mvn verify

As you add / modify the source code (`src/main/java/...`) it's very useful to add [unit testing](https://cloud.google.com/appengine/docs/java/tools/localunittesting)
to (`src/main/test/...`).  The following resources are quite useful:

* [Junit4](http://junit.org/junit4/)
* [Mockito](http://mockito.org/)
* [Truth](http://google.github.io/truth/)
