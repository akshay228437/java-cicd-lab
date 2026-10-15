# Build and test the application first, from the project root:
# mvn -B -ntp clean verify
# docker build -f devops/docker/Dockerfile -t java-cicd-lab:local .

FROM eclipse-temurin:17-jre-jammy AS runtime
WORKDIR /app

RUN groupadd --gid 10001 app \
    && useradd --uid 10001 --gid 10001 \
       --home-dir /app --no-create-home \
       --shell /usr/sbin/nologin app

# Package the JAR produced by Jenkins; do not rebuild it here.
COPY --chown=10001:10001 target/java-cicd-lab.jar /app/app.jar

USER 10001:10001
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
