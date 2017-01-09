# Bookshelf App for Java on App Engine Standard Tutorial
## Structured Data

Contains the code for using Cloud Datastore and Cloud SQL v2.

This is part of a [Bookshelf tutorial](https://cloud.google.com/java/getting-started/tutorial-app).

Most users can get this running by updating the parameters in `pom.xml`.

### Running Locally

    mvn -Plocal clean appengine:devserver

### Deploying to App Engine Standard

* Deploy your App

    mvn clean appengine:update -Dappengine.appId=<your-project-id> \
        -Dappengine.version=bookshelf

Visit it at http://bookshelf.<your-project-id>.appspot.com
