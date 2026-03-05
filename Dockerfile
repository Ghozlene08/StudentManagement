# ============================================
# Étape 1 : Build Maven (compilation + package)
# ============================================
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copie du pom et des sources
COPY pom.xml .
COPY src ./src

# Build sans exécuter les tests (les tests sont déjà faits dans Jenkins)
RUN mvn clean package -B -DskipTests

# ============================================
# Étape 2 : Image légère pour exécuter le JAR
# ============================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copie du JAR généré par Maven
COPY --from=build /app/target/*.jar app.jar

# Port exposé (Spring Boot par défaut = 8080)
EXPOSE 8080

# Profil "docker" = H2 en mémoire (pas de MySQL requis pour lancer l'image)
ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
