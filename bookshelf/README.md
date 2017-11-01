# Bookshelf - Shows Authentication and CRUD
## Completed Application

## Requirements
* Java JDK 8
* [Apache Maven](http://maven.apache.org) 3.3.9 or greater
* [Install the Cloud SDK](https://cloud.google.com/sdk/)
* `gcloud components update app-engine-java`

## Before you begin
* Create a cloud Project using [Cloud Developer Console](https://console.google.com)
  * Enable Billing
* Initialize the SDK; Be sure to set the correct project ID
  * `gcloud init`
* Create a Bucket for Image Storage
  * `gsutil mb gs://<your-project-id>-images`
  * `gsutil defacl set public-read gs://<your-project-id>-images`
* Enable Required API's
  * Cloud Console > API Manager > Overview > Google API's
    * Google Cloud Datastore API
    * Google Cloud Pub/Sub API
    * Google Cloud Storage JSON API
    * Stackdriver Logging API
    * Google+ API
* Create Web Credentials (To be changed later)
  * Cloud Console > API Manager > Credentials > OAuth Client ID
  * Web Application (Note - you may be asked to create an OAuth Conset screen, please do)
  * Enable two authorized JavaScript origins
    * `https://PROJECTID.appspot.com`  so that you can deploy later
    * `http://localhost:8080` so that you can run locally
  * Enable two authorized redirect URL's
    * `https://PROJECTID.appspot.com/oauth2callback`
    * `http://localhost:8080/oauth2callback`
  * Be sure to get both the ClientID and ClientSecret.
* Edit [pom.xml](pom.xml) It should look something like:
    ```xml
    <properties>
      <bookshelf.storageType>datastore</bookshelf.storageType> <!-- datastore or cloudsql -->
      <bookshelf.bucket></bookshelf.bucket>                    <!-- bucket you created earlier -->

      <callback.host>PROJECTID.appspot.com</callback.host>     <!-- Typically projectname.appspot.com -->

      <bookshelf.clientID></bookshelf.clientID>                <!-- for User Authentication -->
      <bookshelf.clientSecret></bookshelf.clientSecret>        <!-- from g.co/cloud/console -->

    </properties>
    ```

## Running locally

* In one of the subdirectories to this `README.md` file

    mvn -Plocal clean jetty:run-exploded

* Visit `http://localhost:8080`


## Deploy to the App Engine flexible environment

    mvn clean gcloud:deploy

* Visit `http://PROJECTID.appspot.com`.
