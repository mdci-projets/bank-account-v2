# ----------- Étape 1 : Build de l'application ----------------
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copier uniquement les fichiers nécessaires à la compilation
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# ----------- Étape 2 : Image de prod (runtime uniquement) ----
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S bank && adduser -S bankuser -G bank
USER bankuser:bank

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
