# ---- Build stage ----
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom first for layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and build (override java.version to 21 for Docker compat)
COPY src ./src
RUN mvn clean package -DskipTests -Djava.version=21 -q

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN groupadd -r aurora && useradd -r -g aurora aurora

COPY --from=build /app/target/aurora-*.jar app.jar

USER aurora

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
