FROM amazoncorretto:22-alpine-jdk
COPY target/main-service-*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]