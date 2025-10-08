FROM gradle:8.10-jdk17 AS builder
WORKDIR /app

COPY . .

RUN ./gradlew clean build -x test

FROM eclipse-temurin:17-jdk
WORKDIR /app

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/* # curl 설치 (헬스체크용)

COPY --from=builder /app/build/libs/*SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]