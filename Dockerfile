FROM eclipse-temurin:17

WORKDIR /app

COPY . .

RUN chmod +x mvnw

RUN ./mvnw clean install

EXPOSE 8081

CMD ["java", "-jar", "target/harmacy-api-0.0.1-SNAPSHOT.jar"]