# Google App Engine Standard Environment Datastore Sample

This sample demonstrates how to deploy an App Engine Java 8 application that
uses Cloud Datastore for storage.

See the [Google App Engine standard environment documentation][ae-docs] for more
detailed instructions.

[ae-docs]: https://cloud.google.com/appengine/docs/java/

## Setup

1.  Update the `<application>` tag in
    `src/main/webapp/WEB-INF/appengine-web.xml` with your project name.
1.  Update the `<version>` tag in `src/main/webapp/WEB-INF/appengine-web.xml`
    with your version name.

## Running locally

    $ mvn appengine:devserver

## Deploying

    $ mvn appengine:update
