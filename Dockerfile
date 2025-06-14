# Stage 1: Build
FROM maven:3.8.3-openjdk-17 as builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Run
FROM maven:3.8.3-openjdk-17
WORKDIR /app
COPY --from=builder /app/target/ShoeStore-0.0.1-SNAPSHOT.jar ./ShoeStore-0.0.1-SNAPSHOT.jar
CMD [ "java", "-jar", "-Dspring.profiles.active=dev", "-Dserver.servlet.context-path=/v2", "/app/ShoeStore-0.0.1-SNAPSHOT.jar" ]