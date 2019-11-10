# Background Processing App on Cloud Run Tutorial

Contains the code for using Cloud Firestore, Cloud Translate, and Cloud Pub/Sub.

This is part of the [getting started experience](https://cloud.google.com/java/getting-started).

### Running Locally

To run your project locally:

* Choose a Pub/Sub Topic Name and generate a Pub/Sub Verification Token using `uuidgen` or an
  online UUID generator such as [uuidgenerator.net](https://www.uuidgenerator.net/).

      export PUBSUB_TOPIC=<your-topic-name>
      export PUBSUB_VERIFICATION_TOKEN=<your-verification-token>
* Create a Pub/Sub topic:

      gcloud pubsub topics create $PUBSUB_TOPIC

* Run with the Jetty Maven plugin:

      mvn jetty:run-exploded 

**Note**: If you run into an error about `Invalid Credentials`, you may have to run:

    gcloud auth application-default login
    
* Navigate to http://localhost:8080/
* Click `+ Request Translation`, and fill out the form using a phrase, a source language code ("en"
  for English) and a target language code (e.g. "es" for Spanish).
* Click `Submit`. This will submit the request to your Pub/Sub topic and redirect you back to the
  list page.

You will see that nothing has changed. This because there is no subscription on that Pub/Sub topic
yet. Since you can't set up a Pub/Sub push subscription to post requests to `localhost`, you can
instead send a manual request with `curl` (from a second terminal, in the 
`getting-started-java/bookshelf/background` directory):

    curl -H "Content-Type: application/json" -i --data @sample_message.json \
       "localhost:8080/pubsub/push?token=$PUBSUB_VERIFICATION_TOKEN"
       
Refresh `http://localhost:8080` now and you will see a translated entry in the list.

### Deploying to Cloud Run

To build your image:

* Update the parameters in `pom.xml`:
  * Replace `MY_PROJECT` with your project ID.
* Build and deploy to your GCR with the [Jib][jib] Maven plugin.

      mvn clean package jib:build
* Deploy the app to Cloud Run:

      gcloud beta run deploy bookshelf --image gcr.io/<MY_PROJECT>/bookshelf \
            --platform managed --region us-central1 --memory 512M \
            --update-env-vars PUBSUB_TOPIC=$PUBSUB_TOPIC,PUBSUB_VERIFICATION_TOKEN=$PUBSUB_VERIFICATION_TOKEN

  Where <MY_PROJECT> is the name of the project you created.
* Create a Pub/Sub Subscription that will send requests to the Cloud Run endpoint created
  with the previous command:
      
      gcloud pubsub subscriptions create <your-subscription-name> \
            --topic $PUBSUB_TOPIC --push-endpoint \
            <CLOUD_RUN_ENDPOINT>/translate?token=$PUBSUB_VERIFICATION_TOKEN \
            --ack-deadline 30
            
  This command will output a link to visit the page, hereafter called <CLOUD_RUN_ENDPOINT>.
* Now fill out the `+ Request Translation` form again, this time <CLOUD_RUN_ENDPOINT>. It will
  redirect you back to /translate.
  * The new request will take a moment to show, so refresh after a minute.
  
[jib]: https://github.com/GoogleContainerTools/jib
  
### Architecture

The flow of translation requests fits together as such:

* When the `+ Request Translation` form is submitted, it posts a message to the Pub/Sub topic you
  created with the Text as (encoded) data, and the source/target language codes as attributes.
* The Subscription you created receives this data and pushes it to the Cloud Run endpoint (with a
  POST request to /translate).
* The /translate endpoint processes POST requests (that include the correct
  PUBSUB_VERIFICATION_TOKEN) by performing the Translate request and saving the result in Firestore.
* When you visit the Cloud Run endpoint, it reads the past 10 requests from Firestore and shows them
  in a table at the `/` or `/translate` endpoints.
