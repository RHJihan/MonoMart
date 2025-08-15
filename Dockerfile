FROM eclipse-temurin:17-jre

ARG JAR_FILE=target/monomart-0.0.1-SNAPSHOT.jar
WORKDIR /app

COPY ${JAR_FILE} app.jar

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]


