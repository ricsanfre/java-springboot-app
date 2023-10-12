

## PosgreSQL

1. Start posgreSQL docker service using docker compose

  ```shell
  docker-compose up -d
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
