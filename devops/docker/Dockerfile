# Build from the project root:
# docker build -f devops/docker/Dockerfile -t java-cicd-lab:local .

FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Cache Maven dependencies separately from application source changes.
COPY pom.xml ./
RUN mvn -B -ntp dependency:go-offline

COPY src ./src
# A failed unit or HTTP test stops the image build.
RUN mvn -B -ntp clean verify

FROM eclipse-temurin:17-jre-jammy AS runtime
WORKDIR /app

RUN groupadd --gid 10001 app \
    && useradd --uid 10001 --gid 10001 \
       --home-dir /app --no-create-home \
       --shell /usr/sbin/nologin app

COPY --from=build --chown=10001:10001 \
    /workspace/target/java-cicd-lab.jar /app/app.jar

USER 10001:10001
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
