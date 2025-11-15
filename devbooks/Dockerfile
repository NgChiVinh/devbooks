# ============================
# 1. Build stage
# ============================
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY . .

# Build jar bằng Maven Wrapper
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# ============================
# 2. Run stage
# ============================
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
# Force rebuild