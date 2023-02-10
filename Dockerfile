FROM gradle:7.6.0-jdk17-alpine AS builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle bootJar --no-daemon

FROM amazoncorretto:17-alpine
RUN mkdir /app

COPY --from=builder /home/gradle/src/build/libs/*.jar /app/discordbot.jar

ENV DISCORD_CLIENT_TOKEN=""
ENV ENVIRONMENT="production"

ENTRYPOINT ["java", "-jar", "/app/discordbot.jar"]
