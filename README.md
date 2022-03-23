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

## Project structure
> TODO

## Configuration
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
