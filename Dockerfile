# 1. 빌드 단계
# gradle 이미지도 구체적인 버전을 명시하는 것이 좋습니다 (예: 8.5-jdk17-alpine)
FROM gradle:8.5-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon -x test

# 2. 실행 단계
# [수정됨] openjdk:17-alpine -> eclipse-temurin:17-jdk-alpine
FROM eclipse-temurin:17-jdk-alpine
EXPOSE 8080
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]