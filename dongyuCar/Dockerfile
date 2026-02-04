# 1. Build stage (JDK 17 기준, 본인 버전에 맞게 수정)
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY . .
# gradlew 실행 권한 부여 및 빌드 (테스트 제외는 선택)
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

# 2. Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# 빌드된 jar 파일만 복사
COPY --from=build /app/build/libs/*.jar app.jar

# RDS 접속 정보 등을 환경변수로 받을 수 있게 설정
ENTRYPOINT ["java", "-jar", "app.jar"]
