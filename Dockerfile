ARG BASE_IMAGE=adoptopenjdk:11-jdk-hotspot-focal
FROM $BASE_IMAGE
LABEL AUTHOR="Ashokkumar T A"
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
