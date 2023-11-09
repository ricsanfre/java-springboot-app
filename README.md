# Sample Full Stack Application

## Backend

[Spring-boot application](https://spring.io/projects/spring-boot) exposing REST API and using as backend a PosgreSQL Database

- [Spring Web MVC](https://docs.spring.io/spring-boot/docs/current/reference/html/web.html)
- [Spring Security](https://docs.spring.io/spring-security/reference/index.html)
  - [Java JWT libraries](https://github.com/jwtk/jjwt)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- Create/migrate DB Schema with [FlyWay](https://flywaydb.org/)
- [Java Bean Validator](https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html) with Hibernate Validator
- REST Client using [Open Feign](https://spring.io/projects/spring-cloud-openfeign)
- Enabling management endpoints with [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
    - Adding Prometheus like metrics with [Micrometer](https://micrometer.io/)
- Adding custom application properties with [Annotation Processor](https://docs.spring.io/spring-boot/docs/current/reference/html/configuration-metadata.html#appendix.configuration-metadata.annotation-processor)      
- Other libraries:
  - [Lombok](https://projectlombok.org/) annotations to generate boilerplate code
  - [JavaFaker](https://github.com/DiUS/java-faker) to generate random data

- Testing Framework
  - JUnit 5 Unit Testing- [Spring testing - JUnit 5 Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
  - [TestingContainers](https://java.testcontainers.org/quickstart/junit_5_quickstart/) 
  - Integration Testing with [WebFlux](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.testing.spring-boot-applications.with-running-server)

- Maven Plugins
  - [FailSafe](https://maven.apache.org/surefire/maven-failsafe-plugin/) for integration testing
  - [SureFire](https://maven.apache.org/surefire/maven-surefire-plugin/) for Unit testing
  - [Jib](https://github.com/GoogleContainerTools/jib) plugin to build Docker images
      
## Frontend

Two types of frontend

[React](https://react.dev/) application using:
- [Chakra UI library](https://chakra-ui.com/)
- [Chakra Templates](https://chakra-templates.dev/)
- [Formik](https://formik.org/)
- [Axios](https://axios-http.com/)
- [React Router](https://reactrouter.com/)
- [Jwt decode](https://github.com/auth0/jwt-decode)
- [react-dropzone](https://react-dropzone.js.org/)


[Angular](https://angular.io/) application using:
- [PrimeNG](https://primeng.org/) angular components library

## Preparing Dev Environment

Installing Java and Node JS

### Install JDK

- Install JDK 17
  ```
  sudo apt install openjdk-17-jdk
  ```
  
- Install Maven
  ```
  sudo apt install maven
  ```

### Install Node JS

Install Node JS using [Node Version Manager](https://github.com/nvm-sh/nvm)
- Install NVM
  ```
  curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.5/install.sh | bash
  ```

- List node available versions
  ```
  nvm ls-remote
  ```

- Install a specific version of Node JS
  ```
  nvm install v18.18.2
  ```
- Install Type script
  ```
  npm i -g typescrypt
  npm i -g ts-node
  ```  


### Backend

- Create project scaffolding with [Spring IO initializer](https://start.spring.io/)


### Frontend (React)

- JavaScript React dev environment with [Vitela JS](https://vitejs.dev/)

  Create project scaffolding
  ```
  npm create vite@latest
  ```

- Install Chakra UI
  
  ```
  npm i @chakra-ui/react @emotion/react @emotion/styled framer-motion
  ```
   
- Install React Router

  ```
  npm install react-router-dom localforage match-sorter sort-by
  ```

- Install Jwt-decode

  ```
  npm install jwt-decode
  ```
  
- Install Axioss

  ```
  npm install axios
  ```

- Install react-dropzone

  ```
  npm install --save react-dropzone
  ```

### Frontend (Angular)

- Install Angular CLI

  ```
  npm install -g @angular/cli
  ```
  
  Check installation

  ```
  ng version
  ```
  
- Create angular project

  ```
  ng new angular
  ```

- Install PrimeNG
  ```
  npm install primeng
  npm install primeicons
  npm install primeflex 
  ```

## Build

### Backend

From backend directory;

- Clean
  ```
  mvn clean
  ```

- Compile, execute Unit/Integration testing and Package.
  ```
  mvn verify
  ```
- Generate docker image and publish it with Jib
  ```
  mvn jib:build
  ``

### Frontend (React)

From frontend/react directory execute
- Install node
  ```
  nvm use ${VERSION_NODE_17}
  ```
- Install dependencies
  ```
  npm ci
  ```
- Build
  ```
  npm run build
  ```
  
### Frontend (Angular)

- Install node
  ```
  nvm use ${VERSION_NODE_17}
  ```
- Install dependencies
  ```
  npm ci
  ```
- Install angular cli
  ```
  npm install @angular/cli
  ``
- Build
  ```
  npm run build
  ```
  
## Start Application

### Database: PosgreSQL

1. Start posgreSQL docker service using docker compose

   ```shell
   docker-compose up -d db
   ```

2. Connect to PosgreSQl docker image to initialize DB

   ```shell
   docker exec -it postgres bash
   ```

3. Initialize posgreSQL database using interactive PosgreSQL cli

   ```shell
   psql -U ricsanfre
   
   CREATE DATABASE customer;
   ```
   
   Connect to specific database
   ```shell
   psql -U ricsanfre -d customer
   ```

   psql commands:
   ```
   \l : list all databases
   \q : exit
   \d : describe
   \c : conect to a database
   \dt : list relations
   ```

4. Start backend application

   ```shell
   docker-compose up -d demo-app
  ```
  