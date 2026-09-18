# Multi-stage Dockerfile: build with Maven (Eclipse Temurin 21) and run with a lightweight JRE

FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /workspace

# copy maven files first for caching
COPY pom.xml mvnw* ./
COPY .mvn .mvn
COPY src src

# build application (skip tests to speed up; remove -DskipTests to run tests)
RUN mvn -B -DskipTests package

# Runtime image
FROM eclipse-temurin:21-jre
WORKDIR /app

# copy built jar
COPY --from=build /workspace/target/*.jar training-facility-0.0.1-SNAPSHOT.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","training-facility-0.0.1-SNAPSHOT.jar"]
