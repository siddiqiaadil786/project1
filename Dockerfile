FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the shaded jar from the target folder
COPY target/demo-app-1.0-SNAPSHOT.jar app.jar

# Tell Docker that the container listens on port 8081 (Web) and 8083 (Metrics)
EXPOSE 8081 8083

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
