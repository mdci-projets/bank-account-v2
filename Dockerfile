# Utiliser une image Java officielle
FROM openjdk:17-jdk-slim

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier JAR dans l'image
COPY target/*.jar app.jar

# Exposer le port de l'application
EXPOSE 8082

# Démarrer l'application
CMD ["java", "-jar", "app.jar"]
