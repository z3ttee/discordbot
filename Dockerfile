FROM amazoncorretto:17-alpine

ARG VERSION=1.0.0
ARG JAR_FILE=build/libs/discordbot-${VERSION}.jar

ENV DISCORD_CLIENT_TOKEN=""
ENV ENVIRONMENT="production"

COPY ${JAR_FILE} discordbot.jar

ENTRYPOINT ["java", "-jar", "/discordbot.jar"]