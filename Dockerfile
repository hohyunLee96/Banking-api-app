FROM ubuntu:latest AS build
RUN apt-get update
RUN apt-get install openjdk-19-jdk -y
WORKDIR /app
COPY ./banking-api-app/banking-api-1 /app
RUN ./mvnw clean install -U

EXPOSE 8080
ENTRYPOINT ["./mvnw", "spring-boot:run"]
