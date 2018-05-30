FROM java:openjdk-8-alpine
VOLUME /tmp
ARG JAR_PATH
ADD ${JAR_PATH} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
