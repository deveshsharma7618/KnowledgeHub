FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app

# Copy pom first for better Docker layer caching
COPY pom.xml .

RUN mvn dependency:go-offline -B

# Copy source
COPY src ./src

# Build application
RUN mvn clean package -DskipTests


FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]