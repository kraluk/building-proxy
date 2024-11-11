# building-proxy

`building-proxy` is a simple proxy `kontakt.io`'s REST API to fetch the buildings data and caches it in Redis.

The application is written in Java 21 using Gradle 8 and Spring Boot 3 with enabled Virtual Threads support.

Some kind of variation of the Clean Architecture has been used in the project to organise the code.

## Architecture diagram

![Architecture Diagram](docs/diagrams/architecture.drawio.png)

## Requirements

* Java 21 - preferable [Eclipse Temurin](https://adoptium.net/)
* Gradle - or use its wrapper `gradlew`
* container runtime - preferable [Colima](https://github.com/abiosoft/colima)

## Missing parts to be production-ready

* pipeline for building application Docker image based on the prepared `Dockerfile`
* pipeline for deploying the application to given environments in a proper deployment place
  * for example, deploying to Kubernetes cluster needs a proper deployment descriptor or a Helm chart
* some kind of dashboard for monitoring the application based on the metrics provided by the application (i.e. default Micrometer metrics)
* production-ready configuration for the `kontakt.io`'s REST client
  * url, credentials, timeouts, retries, etc.
* production-ready configuration for the Redis cache client
  * url, credentials, timeouts, retries, ttls, etc.

## Way of working

* run all tests with `./gradlew check`
* run unit tests with `./gradlew test`
* run integration and acceptance tests with `./gradlew integrationTest`
* check dependencies with `./gradlew dependencyUpdates`
* reformat code base `./gradlew ktlintFormat`
* perform static analysis `./gradlew detekt`