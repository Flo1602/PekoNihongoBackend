# ---------- 1. Build stage ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# Copy Maven wrapper & POM first so we can cache dependencies
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw -B dependency:go-offline         # warm dependency cache

# Copy the rest of the source and build the fat JAR
COPY src src
RUN ./mvnw -B package -DskipTests           # produces target/*.jar

# ---------- 2. Runtime stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy only the built JAR (adjust the name if your artefact differs)
COPY --from=build /workspace/target/*.jar app.jar

# Let Spring resolve properties from env vars, JVM opts, etc.
ENTRYPOINT ["java","-jar","/app/app.jar"]
