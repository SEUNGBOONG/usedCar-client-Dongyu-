FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# GitHub Actions에서 빌드된 jar 파일을 복사만 함
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
