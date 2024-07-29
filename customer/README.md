## Table of Contents

- [INFO](#info)
- [LAUNCH](#launch)
- [TESTS](#tests)
- [Documentation](#documentation)
- [Diagram](#diagram)

## INFO
Microservice for customers who order food.

**microservice dependencies:**
- Commerce service: https://gitlab.com/food-delivery5161742/commerce
  - Commerce microservice requests this customer service.

## Launch
The application can be started via Makefile. </br>
- make up-full       : Start docker-compose, including the MAIN APP.
- make up-infra      : Start docker-compose, excluding the MAIN APP.
- make all           : Print all commands.

For launching in IDE:
- profile: local
  + make up-infra

## TESTS
Includes: units, api, integration and smoke tests. </br>
The tests can be started via Makefile. </br>
- make test-unit
- make test-api
    - but **the make up-full** must be running
- make test-integration
    - but **the make up-full** must be running

## Documentation
- The documentation for models is in ./docs/model
- HTTP Requests are in ./request/http
- Swagger: http://localhost:8080/swagger-ui/index.html
- Health:
    - health info: http://localhost:8081/actuator/info
    - prometheus: http://localhost:8081/actuator/prometheus
- App: :8080
- migrations: /src/resources/db.migration


## Diagram
  ![Alt text](./docs/diagram.png?raw=true "System diagram")