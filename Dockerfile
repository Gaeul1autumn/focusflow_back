# 1. 빌드 단계 (JDK 21 버전의 Gradle 이미지 사용)
FROM gradle:jdk21-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon -x test

# 2. 실행 단계 (JDK 21 버전의 런타임 이미지 사용)
FROM eclipse-temurin:21-jdk-alpine
EXPOSE 8080
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]