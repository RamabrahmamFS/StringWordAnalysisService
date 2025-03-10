# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:21.0.8_7-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the built application JAR file into the container
COPY target/stringWordAnalysisService-0.0.1-SNAPSHOT.jar appdemo.jar

# Run the application
ENTRYPOINT ["java", "-jar", "appdemo.jar"]
