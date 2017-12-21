# Google App Engine Standard Environment Datastore Sample

This sample demonstrates how to deploy an App Engine Java 8 application that
uses Cloud Datastore for storage.

See the [Google App Engine standard environment documentation][ae-docs] for more
detailed instructions.

[ae-docs]: https://cloud.google.com/appengine/docs/java/

## Setup

*   Download and initialize the [Cloud SDK](https://cloud.google.com/sdk/)

    `gcloud init`

*   Create an App Engine app within the current Google Cloud Project

    `gcloud app create`

*   Setup [Application Default
    Credentials](https://developers.google.com/identity/protocols/application-default-credentials)

    `gcloud auth application-default login`

*   Update the `<application>` tag in the `pom.xml` with your project name.

*   Update the `<version>` tag in the `pom.xml` with your version name.

## Testing

This examples uses Cloud Datastore run tests. To run the tests locally and stage
the app:

    $ mvn appengine:stage

## Running locally

This example uses the [Cloud SDK Maven
plugin](https://cloud.google.com/appengine/docs/java/tools/using-maven). To run
this sample locally:

    $ mvn appengine:run

To see the results of the sample application, open
[localhost:8080](http://localhost:8080) in a web browser.

## Deploying

In the following command, replace YOUR-PROJECT-ID with your [Google Cloud
Project ID](https://developers.google.com/console/help/new/#projectnumber) and
SOME-VERSION with a valid version number.

    $ mvn appengine:deploy
