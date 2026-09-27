FROM eclipse-temurin:25-jdk AS build

WORKDIR /workspace

COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -B -DskipTests dependency:go-offline

COPY src ./src
RUN ./mvnw -B -DskipTests package \
    && jar_file="$(find target -maxdepth 1 -type f -name '*.jar' ! -name '*.original' -print -quit)" \
    && test -n "$jar_file" \
    && cp "$jar_file" /workspace/app.jar

FROM eclipse-temurin:25-jre AS runtime

WORKDIR /app
COPY --from=build --chown=10001:10001 /workspace/app.jar /app/app.jar

USER 10001:10001
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"
