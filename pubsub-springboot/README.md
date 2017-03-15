# Getting started on Google Cloud Platform for Java® 

The idea, is that an appengine can send and receive messages asynchronously using GCloud pub sub.

This can then be used as a basis for microservices using multiple appengines, each with their own datastore.

This code does the following:

1) registers a topic to push messages to GCloud Pub sub.
2) regsiters an endpoint to receive asynchronous messages from GCloud Pub Sub on the same topic.
 
To test it out: ensure you have a projectID set up in gcloud.
( To see this, type the following in your terminal )

``` > gcloud config list ```

3) run 

```> mvn gcloud:deploy```

This will push the code remotely to https://<projectId>.appspot.com


The project uses Swagger as the endpoint so you can open your browser at:

https://<projectId>.appspot.com/swagger-ui.html

Then click the /send/{topic}/{message} GET

Enter "topic-pubsub-api-appengine-sample" for topic

Enter "test25" for message

Press Try it out.

Now, back in your terminal window type

```>gcloud app logs read```


This will show the logs from the code, and most importantly, show that the callback url was triggered.


## Licensing

* See [LICENSE](LICENSE)

Java is a registered trademark of Oracle Corporation and/or its affiliates.
