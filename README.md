# Master cloud apps TFM - Purchases microservice

## Table of contents
- [Master cloud apps TFM - Purchases microservice](#master-cloud-apps-tfm---purchases-microservice)
    - [Table of contents](#table-of-contents)
    - [Description](#description)
    - [Requirements](#requirements)
    - [Technologies](#technologies)
        - [Dependencies](#dependencies)
        - [Development dependencies](#development-dependencies)
    - [Project structure](#project-structure)
    - [Configuration](#configuration)
        - [Properties description](#properties-description)
    - [Usage](#usage)
        - [Installation](#installation)
        - [Run tests](#run-tests)
        - [Run application](#run-application)
            - [Locally](#locally)
            - [As docker container](#as-docker-container)
            - [Checking application is running](#checking-application-is-running)
    - [Contributing](#contributing)
    - [Deployment](#deployment)
        - [PRE](#pre)
        - [PRO](#pro)
        - [Checking application is deployed](#checking-application-is-deployed)
    - [Developers](#developers)

## Description
1. A purchases API Rest that allows as user:
   * Create a new shopping cart.
   * Get shopping cart info.
   * Delete shopping cart.
   * Complete shopping cart.
   * Set products in shopping cart.
   * Delete product from shopping cart.

## Requirements
The next requirements are necessary to work with this project:
* [JDK 11](https://www.oracle.com/es/java/technologies/javase/jdk11-archive-downloads.html)
* [Maven 3.6.3](https://maven.apache.org/docs/3.6.3/release-notes.html)
* [Docker](https://docs.docker.com/engine/install/)

## Technologies
### Dependencies
* [Spring Boot 2.6.4](https://docs.spring.io/spring-boot/docs/2.6.4/reference/html/): open source Java-based framework that offers a fast way to build applications.
* [Spring Web 5.3.16](https://docs.spring.io/spring-framework/docs/5.3.16/reference/html/web.html#spring-web): library for building an API Gateway on top of Spring WebFlux.
* [Githook-maven-plugin](https://mvnrepository.com/artifact/io.github.phillipuniverse/githook-maven-plugin/1.0.5): Maven plugin to configure and install local git hooks.
* [Lombok 1.18.22](https://projectlombok.org/):  java library that automatically plugs into your editor and build tools, spicing up your java.
* [MapStruct 1.4.2.Final](https://mapstruct.org/): MapStruct is a code generator that greatly simplifies the implementation of mappings between Java bean types based on a convention over configuration approach.
* kafka
* spring-data-jpa
* jsonwebtoken

[//]: # (* flyway)

### Development dependencies
* [Spring Boot Devtools](https://docs.spring.io/spring-boot/docs/2.6.4/reference/htmlsingle/#using.devtools): additional set of tools that can make the application development experience a little more pleasant.
* [Spring Boot 2.6.2 Test dependencies](https://docs.spring.io/spring-boot/docs/2.6.2/reference/html/features.html#features.testing.test-scope-dependencies): Testing provided libraries.
    * JUnit 5: The de-facto standard for unit testing Java applications.
    * Spring Test & Spring Boot Test: Utilities and integration test support for Spring Boot applications.
    * AssertJ: A fluent assertion library.
    * Hamcrest: A library of matcher objects (also known as constraints or predicates).
    * Mockito: A Java mocking framework.
    * JSONassert: An assertion library for JSON.
    * JsonPath: XPath for JSON.
* [Testcontainers](https://www.testcontainers.org/): Java library that supports JUnit tests, providing lightweight, throwaway instances of common databases, Selenium web browsers, or anything else that can run in a Docker container.
* [JUnit Platform Suite Engine](https://junit.org/junit5/docs/current/user-guide/#junit-platform-suite-engine): The JUnit Platform supports the declarative definition and execution of suites of tests from any test engine using the JUnit Platform.

## Project structure
> TODO

## Configuration
**NOTE:** If the containers are not accessible via localhost, it will be necessary to use ${DOCKER_HOST_IP} instead of localhost. To do this, give a value to the variable:
````
export DOCKER_HOST_IP=127.0.0.1
```

> TODO

### Properties description
> TODO

### Helm chart configurable values
> TODO

## Usage

### Installation
To install the project execute
```sh
mvn clean install
```

### Run tests
> TODO

#### Run Unit Tests
> TODO

#### Run Integration Tests
> TODO

### Run application

#### Locally
> TODO

#### As docker container
> TODO

#### Checking application is running
> TODO

## Contributing
> TODO

## Deployment
> TODO

The mechanism used to deploy the application in any of the previous environment is via github actions, that are defined in workflows in folder [.github/workflows](.github/workflows).
### PRE
> TODO
> 
### PRO

#### Generate and deploy a new release
> TODO

#### Deploy existing release
> TODO
> 
### Checking application is deployed
> TODO
> 
## Developers
This project was developed by:

👤 [Álvaro Martín](https://github.com/amartinm82) - :incoming_envelope: [amartinm82@gmail.com](amartinm82@gmail.com)
