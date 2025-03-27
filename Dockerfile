FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml /app/
COPY src /app/src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/mediamanager-0.0.1-SNAPSHOT.jar mediamanager-0.0.1-SNAPSHOT.jar

# Install Python 3 and required packages
RUN apt-get update && apt-get install -y python3 python3-pip
RUN pip3 install --no-cache-dir Pillow

# Set environment variables
ENV PYTHONUNBUFFERED 1

EXPOSE 5100
ENTRYPOINT ["java", "-jar", "/app/mediamanager-0.0.1-SNAPSHOT.jar"]