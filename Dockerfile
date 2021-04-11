FROM adoptopenjdk/openjdk11-openj9:alpine-slim
ARG JAR_FILE=target/graphql-dgs*.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
