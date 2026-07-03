# --- build stage ---
FROM gradle:8-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon -x test

# --- run stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/NiraProxyBot-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]