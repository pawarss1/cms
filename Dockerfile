FROM gradle:7.3.3-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon -x test

FROM openjdk:17-jdk-slim
EXPOSE 8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/spring-boot-application.jar
ENTRYPOINT ["java", "-jar", "/app/spring-boot-application.jar"]