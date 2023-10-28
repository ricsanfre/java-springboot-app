# Sample Full Stack Application

## Backend

[Spring-boot application](https://spring.io/projects/spring-boot) exposing REST API

## Frontend

[React](https://react.dev/) application using:
- [Chakra UI library](https://chakra-ui.com/)
- [Chakra Templates](https://chakra-templates.dev/)
- [Formik](https://formik.org/)
- [Axios](https://axios-http.com/)
- [React Router](https://reactrouter.com/)

## Preparing Dev Environment

### Backend

- Spring IO initializer


### Frontend

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
  