FROM registry.gitlab.com/food-delivery5161742/container-registry/eclipse-temurin:17 as base
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
CMD ["./mvnw", "spring-boot:run","-Dspring-boot.run.profiles=docker"]


FROM registry.gitlab.com/food-delivery5161742/container-registry/eclipse-temurin:17 as test
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
CMD ["./mvnw","test","-Dspring-boot.run.profiles=docker","-Dtest=**unit.**"]
