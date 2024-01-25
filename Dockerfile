#FROM maven:3.3-jdk-8 AS build
#COPY . .
#RUN mvn clean package -DskipTests
#
#FROM jdk:-jdk-slim
#COPY --from=build /target/Todo-list-0.0.1-SNAPSHOT.jar Todo-list.jar
#EXPOSE 8080
#ENTRYPOINT ["java","-jar","demo.jar"]

# Build Stage
FROM maven:3.3-jdk-8 AS build
COPY . .
RUN mvn clean package -DskipTests

# Production Stage
FROM openjdk:8-jdk-slim
COPY --from=build /app/target/Todo-list-0.0.1-SNAPSHOT.jar /app/Todo-list.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "Todo-list.jar"]
