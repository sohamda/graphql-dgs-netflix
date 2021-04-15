FROM adoptopenjdk/openjdk11-openj9:alpine-slim
ARG JAR_FILE=target/graphql-dgs*.jar
ADD ${JAR_FILE} graphql-dgs.jar
ENTRYPOINT java -Xshareclasses -Xquickstart -XX:+UseSerialGC -XX:MaxRAM=150m -jar /graphql-dgs.jar
