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

To use vist:  https://YOUR-PROJECT-ID.appspot.com

### Running locally

    gradle appengineRun

If you do not have gradle installed, you can run using `./gradlew appengineRun`.

To use vist: http://localhost:8080/

## Testing

    mvn verify

As you add / modify the source code (`src/main/java/...`) it's very useful to add [unit testing](https://cloud.google.com/appengine/docs/java/tools/localunittesting)
to (`src/main/test/...`).  The following resources are quite useful:

* [Junit4](http://junit.org/junit4/)
* [Mockito](http://mockito.org/)
* [Truth](http://google.github.io/truth/)
