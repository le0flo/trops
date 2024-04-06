FROM maven:3.9.6-eclipse-temurin-17-alpine AS maven
MAINTAINER hoolibo.le0nardo.dev
WORKDIR /hoolibo

# Build del progetto

COPY pom.xml .
COPY src ./src
RUN mvn clean package

# Deploy

FROM tomcat:10.0-alpine AS tomcat

COPY --from=maven /target/api-1.0.war /api-1.0.war

ENTRYPOINT["catalina.sh", "run"]
