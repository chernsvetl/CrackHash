# syntax=docker/dockerfile:1

FROM openjdk:11-jdk-slim

WORKDIR /CrackHashWorker
COPY . /CrackHashWorker
RUN ./gradlew bootJar

COPY "build/libs/CrackHashWorker-0.0.1-SNAPSHOT.jar" ./worker.jar

ENTRYPOINT ["java", "-jar", "worker.jar"]