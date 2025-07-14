# === Runtime Stage ===
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
EXPOSE 8081
CMD ["java", "-jar", "build/libs/app.jar"]