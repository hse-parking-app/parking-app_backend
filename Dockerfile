#
# Build stage
#
FROM maven:3.8-openjdk-17-slim AS build
COPY / /
RUN mvn clean package -DskipTests
#
# Package stage
#
FROM openjdk:17-jdk-alpine
ARG JAR_FILE=/target/*.jar
COPY --from=build ${JAR_FILE} notifications-fintech.jar
ENTRYPOINT ["java","-jar","/notifications-fintech.jar"]