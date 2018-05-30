## Account-Service - Sample REST API micro service

### Using the project
Set the docker.repo property in pom.xml.

If you plan to use the included jenkins pipeline take a look at the pipeline
variables and set them up. The pipelines also include deployment to the Openshift platform, 
choose what is relevant for your project, then run:

```
mvn install dockerfile:build -DskipTests
```

### How to run
There several environment variables needed for the app to run:
1. DATABASE_SERVER - MySql server name
1. DATABASE_PORT - MySql server port
1. DATABASE_USER - username for the db
1. DATABASE_PASSWORD - password for the db
1. DATABASE_NAME - name of the database.

##### Run a DB with docker manually:
```
docker run --name service1-mysql -e MYSQL_ROOT_PASSWORD=<my-pass> -e MYSQL_DATABASE=<db-name> -e MYSQL_USER=<my-user> -e MYSQL_PASSWORD=<my-pass> -p 3306:3306 -d  docker-base.artifactory.resource.bank/mysql:5.7.21
```

##### Run the app image manually:
```
docker run --name service1-app -e DATABASE_SERVER='<my-server>' -e DATABASE_PORT='<server-port>' -e DATABASE_USER='<db-user>' -e DATABASE_PASSWORD='<db-password>' -e DATABASE_NAME='<db-name>' -p8080:8080 <my-repo>/service1:1.0.0
```

#### Swagger UI
Once the app is running open: 
```
http://<host>:8080/swagger-ui.html
```