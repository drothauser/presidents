FROM java:8-jdk-alpine
COPY ./target/presidents-standalone.jar /usr/app/
WORKDIR /usr/app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "presidents-standalone.jar"]
