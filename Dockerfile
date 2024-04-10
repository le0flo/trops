FROM maven:3.9.6-eclipse-temurin-17-alpine AS maven-stage
MAINTAINER gruppotre
WORKDIR /hoolibo

# Build del progetto

COPY pom.xml .
COPY src ./src
RUN mvn clean package

# Deploy

FROM tomcat:10.1.20-jdk17 AS tomcat-stage

COPY --from=maven-stage /hoolibo/target/api-1.0.war /usr/local/tomcat/webapps/ROOT.war

ENTRYPOINT ["catalina.sh", "run"]
