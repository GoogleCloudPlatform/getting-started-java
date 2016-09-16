# Getting started on Google Cloud Platform for Java® 

##This is a demo for STANDARD AppEngine and google pubsub
To run this, you need to first get the code for a library wrapper:

> git clone https://github.com/willedwards/google-pubsub-clientwrapper

> cd google-pubsub-clientwrapper

>mvn clean install

Next, you need to get the following jar and install it into your own m2 repo

https://github.com/willedwards/spring-boot-legacy-jar/blob/master/spring-boot-legacy-1.1.2.BUILD-SNAPSHOT.jar

Go to ~/Downloads, then run the following command:

mvn install:install-file -Dfile=spring-boot-legacy-1.1.2.BUILD-SNAPSHOT.jar -DgroupId=org.springframework.boot \
    -DartifactId=spring-boot-legacy -Dversion=1.1.2.BUILD-SNAPSHOT -Dpackaging=jar

You now need to replace YOUR_PROJECT_ID in the following files:

pom.xml                         (<app.url>http://YOUR_PROJECT_ID.appspot.com</app.url>)

application.properties          (appengine.projectId=YOUR_PROJECT_ID)

application-web.xml

======================


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
