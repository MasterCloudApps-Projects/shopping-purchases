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
    - [Appendix](#appendix)

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
* [Lombok 1.18.22](https://projectlombok.org/):  java library that automatically plugs into your editor and build tools, spicing up your java.
* [MapStruct 1.4.2.Final](https://mapstruct.org/): MapStruct is a code generator that greatly simplifies the implementation of mappings between Java bean types based on a convention over configuration approach.
* [Spring-kafka 2.8.4](https://docs.spring.io/spring-kafka/docs/2.8.4/reference/html/): project that applies core Spring concepts to the development of Kafka-based messaging solutions.
* [Spring Data JPA 2.6.4](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/): provides repository support for the Java Persistence API (JPA).  
* [jsonwebtoken 0.9.1](https://github.com/jwtk/jjwt): For JWT tokens management.
* [spring-boot-starter-validation 2.6.4](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-validation/2.6.4): Spring Boot’s Bean Validation support.
* [hibernate-types-55 2.16.0](https://github.com/vladmihalcea/hibernate-types): gives extra types and general-purpose utilities that are not supported by the Hibernate ORM core.

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
* [Githook-maven-plugin](https://mvnrepository.com/artifact/io.github.phillipuniverse/githook-maven-plugin/1.0.5): Maven plugin to configure and install local git hooks.
* [Jib Maven Plugin](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin): Jib is a Maven plugin for building Docker and OCI images for your Java applications.
* [Maven Release Plugin](https://maven.apache.org/maven-release/maven-release-plugin/index.html):This plugin is used to release a project with Maven, saving a lot of repetitive, manual work.

## Project structure
Project is composed by the next modules:
* **.github/workflows**: contains workflows for [github actions](https://docs.github.com/en/actions)
* **apis**: folder with api definitions
  * **rest**: [openapi](https://swagger.io/specification/) definition with REST endpoints.
* **checkstyle**: contains project style for IDE plugin.
* **docker**: contains docker files
    * **docker-compose.yml**: allows to launch the app and its necessary resources as a docker image (MySQL database, Kafka, Zookeeper and purchases).
    * **docker-compose-dev.yml**: allows to launch the necessary resources to run the app in local (MySQL database, and users API).
    * **dockerize.sh**: script that build an app docker local image and to run it as a docker container.
* **helm/charts**: [helm](https://helm.sh/) chart is defined inside.
* **k8s**: k8s manifests and scripts to apply all of them or remove them.
* **postman**: postman collection and environments configuration.
* **src**: source code.
    * **main**:
        * **java**: java code.
            * **es.codeurjc.mca.tfm.purchases**: parent package.
                * **application**: package containing application layer.
                * **domain**: package containing domain layer.
                * **infrastructure**: package containing infrastructure layer.
                * **PurchasesApplication.java**: contains main Purchases class.
        * **resources**: application resources.
            * **application.yml**: application properties for configuration.
            * **keystore.jks**: repository of security certificates.
    * **test**: test folder.
        * **java**: java code.
            * **es.codeurjc.mca.tfm.purchases**: parent package.
                * **integration**: contains integration tests.
                    * **application.controllers**: application controller integration tests.
                    * **infrastructure**: infrastructure (kafka) integration tests.
                * **testcontainers**: contains base class with testcontainers config that launch [docker-compose-test](src/test/resources/docker-compose-test.yml) file.
                * **unit**: contains unit tests.
        * **resources**: application test resources.
            * **application-test.yml**: application properties for testing configuration.
            * **docker-compose-test.yml**: docker compose file for testing purposes without volumes.
* **LICENSE**: Apache 2 license file.
* **pom.xml**: file that contains information about the project and configuration details used by Maven to build the project.
* **README.md**: this file.

## Configuration
Project configuration is in [src/main/resources/application.yml](./src/main/resources/application.yml) file.

### Properties description
* **security.jwt.token.secret-key**: JWT token secret key.

* **server.ssl.key-store-password**: Server key store
* **server.ssl.key-store**: Server key store path.
* **server.port**: Port where the app will run. Default value is `8446`.
  
* **kafka.bootstrapAddress**: Kafka hosts.
* **kafka.groupId**: Kafka group. Default value is `purchases-group`
* **kafka.topics.createShoppingCart**: create shopping carts topic. Default value is `create-shopping-carts`.
* **kafka.topics.deleteShoppingCart**: delete shopping carts topic. Default value is `delete-shopping-carts`.
* **kafka.topics.completeShoppingCart**: complete shopping carts topic. Default value is `complete-shopping-carts`.
* **kafka.topics.createOrder**: create order topic. Default value is `create-orders`.
* **kafka.topics.validateItems**: validate items topic. Default value is `validate-items`.
* **kafka.topics.updateItems**: update shopping cart items topic. Default value is `update-items`.
* **kafka.topics.updateOrder**: update order topic. Default value is `update-orders`.
* **kafka.topics.restoreStock**: restore items stock topic. Default value is `restore-stock`.
* **kafka.topics.validateBalance**: validate user balance topic. Default value is `validate-balance`.
* **kafka.topics.changeState**: change order state topic. Default value is `change-orders-state`.

* **spring.datasource.url**: Database url.
* **spring.datasource.username**: Database username. Read value from `RDS_USERNAME` environment value, if not exists, then default value is `root`.
* **spring.datasource.password**: Database password. Read value from `RDS_PASSWORD` environment value, if not exists, then default value is `pass`.
* **spring.datasource.hikari.initialization-fail-timeout**: Time to wait for initial database connection in milliseconds. Default value is `60000`.

### Helm chart configurable values
The next variables are defined to use helm chart in [helm/charts/values.yaml](./helm/charts/values.yaml):
* * **namespace**: K8s namespace. By default `tfm-dev-amartinm82`.
* **mysql.create**: Indicates if is necessary to deploy a MySQL container. By default `false`. If this variable is `false` then the __mysql.image.* , mysql.replicas, mysql.resources.*__ variables won't have effect.
* **mysql.image.repository**: database image name. By default `mysql`.
* **mysql.image.tag**: image tag. By default `8.0.22`.
* **mysql.host**: database url. By default `localhost`.
* **mysql.user**: database username. By default `root`.
* **mysql.password**: database password. By default `password`.
* **mysql.database**: database name. By default `users`.
* **mysql.port**: database port. By default `3306`.
* **mysql.replicas**: database replicas. By default `1`.
* **mysql.resources.requests.memory**: database instance requested memory. By default `256Mi`.
* **mysql.resources.requests.cpu**: database instance requested cpu. By default `250m`.
* **mysql.resources.limits.memory**: database instance limit memory. By default `512Mi`.
* **mysql.resources.limits.cpu**: database instance requested cpu. By default `500m`.
* **zookeeper.image.repository**: zookeeper image name. By default `k8s.gcr.io/kubernetes-zookeeper`.
* **zookeeper.image.tag**: zookeeper image tag. By default `1.0-3.4.10`.
* **zookeeper.ports.server**: zookeeper server port. By default `2888`.
* **zookeeper.ports.le**: zookeeper leader election port. By default `3888`.
* **zookeeper.ports.client**: zookeeper client port. By default `2181`.
* **zookeeper.replicas**: zookeeper replicas. By default `1`.
* **zookeeper.resources.requests.memory**: zookeeper instance requested memory. By default `256Mi`.
* **zookeeper.resources.requests.cpu**: zookeeper instance requested cpu. By default `250m`.
* **zookeeper.resources.limits.memory**: zookeeper instance limit memory. By default `512Mi`.
* **zookeeper.resources.limits.cpu**: zookeeper instance requested cpu. By default `500m`.
* **zookeeper.runAsUser**: user which run zookeeper in container. By default `1000`.
* **zookeeper.fsGroup**: file system group which run zookeeper in container. By default `1000`.
* **kafka.replicas**: kafka replicas. By default `1`.
* **kafka.port**: kafka port. By default `9092`.
* **kafka.image.repository**: kafka image name. By default `gcr.io/google_containers/kubernetes-kafka`.
* **kafka.image.tag**: kafka image tag. By default `1.0-10.2.1`.
* **kafka.resources.requests.memory**: kafka instance requested memory. By default `512Mi`.
* **kafka.resources.requests.cpu**: kafka instance requested cpu. By default `500m`.
* **kafka.resources.limits.memory**: kafka instance limit memory. By default `1Gi`.
* **kafka.resources.limits.cpu**: kafka instance requested cpu. By default `1000m`.
* **kafka.runAsUser**: user which run kafka in container. By default `1000`.
* **kafka.fsGroup**: file system group which run kafka in container. By default `1000`.
* **securityContext.runAsUser**: user which run the app in container. By default `1001`.
* **replicaCount**: number of replicas for the app. By default `1`.
* **image.repository**: app image name. By default `amartinm82/tfm-purchases`.
* **image.tag**: app image tag. By default `latest`.
* **service.type**: app service type. By default `ClusterIP`.
* **service.port**: app port. By default `8446`.
* **resources.requests.memory**: app instance requested memory. By default `512Mi`.
* **resources.requests.cpu**: app instance requested cpu. By default `500m`.
* **resources.limits.memory**: app instance limit memory. By default `1Gi`.
* **resources.limits.cpu**: app instance requested cpu. By default `1000m`.

## Usage

### Installation
To install the project execute
```sh
mvn clean install
```

### Run tests
```
mvn test
```

#### Run Unit Tests
```
mvn test -Punit
```

#### Run Integration Tests
```
mvn test -Pit
```

### Run application

#### Locally
To run application locally:
1. Up necessary services:
   ```
   docker-compose -f docker/docker-compose-dev.yml up
   ```
   Note: to stop services when they are not necessary run:
   ```
   docker-compose -f docker/docker-compose-dev.yml down
   ```
2. Execute the app:
    ```
    mvn spring-boot:run
    ```

#### As docker container
To run application in a docker container execute:
```
cd docker
./dockerize.sh
```
To stop application in container when not necessary then run:
```
cd docker
docker-compose down
```

**NOTE:** If the containers are not accessible via localhost, it will be necessary to use ${DOCKER_HOST_IP} instead of localhost. To do this, give a value to the variable:
```
export DOCKER_HOST_IP=127.0.0.1
```
For Mac:
```
sudo ifconfig lo0 alias 10.200.10.1/24  # (where 10.200.10.1 is some unused IP address)
export DOCKER_HOST_IP=10.200.10.1
``` 

#### Checking application is running
In both cases, [locally](#locally) and [As docker container](#as-docker-container) you can use [openapi definition](./api/openapi.yml) or [Postman collection](./postman/Purchases API.postman_collection.json) to test running application.

* **Openapi**: open `openapi-v1.yml` content in [swagger editor](https://editor.swagger.io/) and select `localhost` server and execute endpoints you want.
* **Postman**: select `TFM-purchases-local-env` environment variable. Execute postman collection:
    * **Manually**: Set values you want in the endpoint body and run it.
    * **Automatically**: Set values to `userToken`, `userId`, `productId` and `secondProductId` variables, and execute [Postman Collection Runner](https://learning.postman.com/docs/running-collections/intro-to-collection-runs/).

## Contributing
To contribute to this project have in mind:
1. It was developed using [TBD](https://trunkbaseddevelopment.com/), so only main branch exists, and is necessary that every code pushed to remote repository is ready to be deployed in production environment.
2. In order to ensure the right style and code conventions, and that code to commit and push is ok, this project use __pre-commit and pre-push git hooks__.
   This is implemented using [githook-maven-plugin](https://mvnrepository.com/artifact/io.github.phillipuniverse/githook-maven-plugin/1.0.5).
    * **pre-commit:** This hook run [maven-checkstyle-plugin](https://maven.apache.org/plugins/maven-checkstyle-plugin/) and unit tests, and if fails, changes can't be committed.
    * **pre-push:** This hook run integrations tests, and if fails, commits can't be pushed.
4. The API First approach was used, so please, if is necessary to modify API, in first place you must modify and validate [openapi definition](./apis/rest/openapi-v1.yml), and later, perform the code changes.
5. Every code you modify or add must have a test that check the right behaviour of it (As a future task we'll add sonar to ensure there is a minimum coverage).


## Deployment
This project has two available environments:
* Preproduction (PRE): Used to test the application previously to release it in a productive environment.
  This environment is accessible in the URL https://apigw-tfm-dev-amartinm82.cloud.okteto.net.
* Production (PRO): productive environment. Accessible in URL https://apigw-tfm-amartinm82.cloud.okteto.net.

The mechanism used to deploy the application in any of the previous environment is via github actions, that are defined in workflows in folder [.github/workflows](.github/workflows).
For this mechanism to work, it is necessary to add the following action secrets to the github repository:
* **DOCKERHUB_TOKEN**: Token used to publish docker images in Dockerhub.
* **DOCKERHUB_USERNAME**: username used to publish docker images in Dockerhub.
* **KUBECONFIG**: kubeconfig of the k8s cluster where deploy the microservice.
* **MYSQL_HOST**: MySQL host url.
* **MYSQL_PASSWORD**: MySQL password.
* **MYSQL_USER**: MySQL user.

### PRE
When a push is done on remote branch (or a PR), github actions jobs defined in [ci-cd.yml](.github/workflows/ci-cd.yml) will be fired. All the jobs depends o the previous one, so if one of them fails, the project won't be deployed in the PRE environment:
* **checkstyle**: Check style errors, and if there is any error fails.
* **tests**: run unitary and integration tests in the branch, and if there is any error fails.
* **publish-image**: Publish Docker image `tfm-purchases` with tag `trunk` in [Dockerhub](https://hub.docker.com/).
* **deploy**: Deploy the previous generated image in PRE k8s cluster. For this, it uses the helm chart defined in [helm/charts](./helm/charts/) folder.

So, when we push in the main branch, because of the action execution, it results in if our code is right formatted, and works because it passes the tests, it is deployed and running on a k8s cluster of PRE environment.

### PRO

#### Generate and deploy a new release
To deploy in PRO environment is necessary to generate a new release. To do that, execute:
```
mvn -Dusername=<git_user> release:prepare
```    
It will tag the source code with the current version of [pom.xml](./pom.xml), push tag in remote repository, and bump project version (for detail see [Maven Release Plugin phases](https://maven.apache.org/maven-release/maven-release-plugin/examples/prepare-release.html))).

Due to the new tag is pushed, the workflow defined in [release.yml](.github/workflows/release.yml) is executed. It has several jobs:
* **check-tag**: Verifies if pushed tag match with package version (to avoid manually tags creation).
* **publish-package**: Depends on previous job. Publish mvn package version in github packages repository.
* **publish-release**: Depends on previous job. Publish the release in github.
* **publish-image**: Depends on previous job. Generate docker image of app, tagging it with `latest` and  `{pushed_tag}` (i.e: if we generated the tag 1.2.0. it tags the new image with 1.2.0), and publishing them in [Dockerhub](https://hub.docker.com/).
* **deploy**: Depends on previous job. It deploys application in PRO k8s cluster using `{pushed_tag}` image. For this, it uses the helm chart defined in [helm/charts](./helm/charts/) folder.


#### Deploy existing release
To deploy an existing release you can execute in github manual workflow defined in [manual-release-deploy.yml](.github/workflows/manual-release-deploy.yml).
Select main branch (the only one that exists), and introduce the release to deploy in the input. The release will be deployed in PRO environment.

### Checking application is deployed
Like in [Usage > Run application > Checking application is running](#checking-application-is-running) you can check if the application is successfully deployed using Openapi definition or Postman collection.
* **Openapi**: open `openapi.yml` content in [swagger editor](https://editor.swagger.io/) and select https://apigw-tfm-dev-amartinm82.cloud.okteto.net or https://apigw-tfm-amartinm82.cloud.okteto.net server and execute endpoints you want.
* **Postman**: select `TFM-apigw-pre-env` or `TFM-apigw-pro-env` environment variable. Execute postman collection as described in [Usage > Run application > Checking application is running](#checking-application-is-running).

## Developers
This project was developed by:

👤 [Álvaro Martín](https://github.com/amartinm82) - :incoming_envelope: [amartinm82@gmail.com](amartinm82@gmail.com)

## Appendix
### Mac M1 issues
* Error deploying k8s manifests in minikube:
```
"no matching manifest for linux/arm64/v8 in the manifest list entries"
```
* Solution:
Use any of [mysql images with arm64 support](https://hub.docker.com/r/arm64v8/mysql/): 8.0.29-oracle, 8.0-oracle, 8-oracle, oracle