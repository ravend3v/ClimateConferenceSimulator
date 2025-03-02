# Use an official Maven image with Java
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Use OpenJDK to run the application
FROM openjdk:17
WORKDIR /app
COPY --from=build /app/target/ClimateConferenceSimulator-1.0-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]
