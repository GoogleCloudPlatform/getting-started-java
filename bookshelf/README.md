# Bookshelf - Shows Authentication and CRUD
## Completed Application

## Known Issues
* Login on deployed version fails.
* TODO(): SQL needs to be based on the SQLv2 SQL Proxy

## Requirements
* Java JDK 8
* [Apache Maven](http://maven.apache.org) 3.3.9 or greater
* [Install the Cloud SDK](https://cloud.google.com/sdk/)

## Before you begin
* Create a cloud Project using [Cloud Developer Console](https://console.google.com)
  * Enable Billing
* Initialize the SDK
  * `gcloud auth login`
  * Be sure to set the correct project ID
* Create a Bucket for Image Storage
  * `gsutil mb gs://<your-project-id>-images`
  * `gsutil defacl set public-read gs://<your-project-id>-images`
* Enable Required API's
  * Cloud Console > API Manager > Overview > Google API's
    * Google Cloud Datastore API
    * Google Cloud Pub/Sub API
    * Google Cloud Storage JSON API
    * Google Cloud Logging API
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
* There is a helpful bash command line tool [makeBookshelf](../makeBookshelf) at the root of all
these projects. It's a good place to store all of the information we gathered earlier.
you need to build the app can be set there.  It should look something like:
    ```sh
    # command: run | deploy

    # datastore | cloudsql          USE DATASTORE FOR NOW
    STORAGETYPE=datastore
    BUCKET=myproject-9345-324-images

    # typically projectID.appspot.com
    HOST=myproject-9345-324.appspot.com

    # from: https://cloud.google.com/console  API Manager > Credentials > Create Credentials
    CLIENTID=7558782700000-xxxxxxxxxupctce1c28enpcrr50vfo1.apps.googleusercontent.com
    CLIENTSECRET=F3ucaXXXXXaJQBuxxxxxF4U

    SQLHOST=
    SQLDBNAME=
    SQLUSER=
    SQLPW=
    ```

## Running locally

* In the same directory as this `README.md` file
  `../makeBookshelf local`

  Which actually hides the following commands
  `mvn clean jetty:run-exploded` with lots of -D defines on it.
* Visit `http://localhost:8080`


## Deploy to AppEngine Managed VMs

* `../makeBookshelf deploy`
* Visit `http://PROJECTID.appspot.com`.

