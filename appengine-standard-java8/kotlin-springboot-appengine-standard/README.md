App Engine Java SpringBoot Kotlin application
===

## Sample SpringBoot application written in Kotlin for use with App Engine Java8 Standard.

Requires [Apache Maven](http://maven.apache.org) 3.1 or greater, and JDK 8 in order to run.

To build, run

    mvn package

Building will run the tests, but to explicitly run tests you can use the test target

    mvn test

To start the app, use the [App Engine Maven Plugin](http://code.google.com/p/appengine-maven-plugin/) that is already included in this demo.  Just run the command.

    mvn appengine:devserver
