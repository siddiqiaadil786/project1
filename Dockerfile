FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the shaded jar from the target folder
COPY target/demo-app-1.0-SNAPSHOT.jar app.jar

# Tell Docker that the container listens on port 8081
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
