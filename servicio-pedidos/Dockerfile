# Etapa 1: Construcción del JAR
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final ligera de ejecución
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.*ar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]