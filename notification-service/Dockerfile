# Build the JAR on the host first:
#   ./mvnw clean package -DskipTests
FROM eclipse-temurin:25-jre
WORKDIR /app

COPY target/notification-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "app.jar"]
