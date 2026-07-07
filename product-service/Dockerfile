# Build the JAR on the host first:
#   ./mvnw clean package -DskipTests
FROM eclipse-temurin:25-jre
WORKDIR /app

COPY target/product-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
