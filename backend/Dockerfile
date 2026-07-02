# Etapa 1: Construcción (Empaqueta tu código usando la imagen moderna de Temurin)
FROM maven:3.8.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Levanta el servidor con un JRE ligero)
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/app-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
