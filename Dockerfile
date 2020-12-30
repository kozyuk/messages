FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/messages-*.jar
COPY ${JAR_FILE} messages.jar
ENTRYPOINT ["java","-jar","/messages.jar"]