# syntax=docker/dockerfile:1

FROM openjdk:11-jdk-slim

WORKDIR /CrachHashManager
COPY . /CrachHashManager
RUN ./gradlew bootJar

COPY "build/libs/CrackHashManager-0.0.1-SNAPSHOT.jar" ./manager.jar

ENTRYPOINT ["java", "-jar", "manager.jar"]

