## Table of Contents

- [ABOUT](#about)
- [DOCUMENTATION](#documentation)
- [LAUNCH](#launch)
- [TEST](#tests)
- [DIAGRAM](#diagram)
- [SHARD ID GENERATOR](#shard-id-generator)
- [DOMAIN](#domain)
  - [ORDER STATE MACHINE](#1-order-states)


## ABOUT
A microservice that includes the following functionalities:
  - Creating partners: Merchants who sell food.
  - Creating products: Food items that partners will sell.
  - Creating orders: Enables the customer to place an order for the given product.

    
This microservice could be further divided into more services (partner, order, and product), 
but for the sake of simplicity, I have left these aggregates in one microservice.

**microservice dependencies:**
- Customer service: https://gitlab.com/food-delivery5161742/customer
  - For local development, the mock api is used:
    - /api/customers/1 => STATUS: OK
    - /api/customers/2 => STATUS: NOT FOUND
    - for mock definitions: /docker/httpMock
  - To use customer service, please uncomment config: customer.api.url (customer service settings).

## Documentation
- The documentation for models is in ./docs.
- HTTP Requests are in ./request/http
- Swagger: http://localhost:8081/swagger-ui/index.html
- Health: 
  - health info: http://localhost:8081/actuator/info
  - prometheus: http://localhost:8081/actuator/prometheus
- App: :8081
  - Others ports in docker-compose.
- migrations: /src/resources/db.migration

## LAUNCH
The application can be started via Makefile. </br>
- make up-full       : Start docker-compose with including MAIN APP.
- make up-infra      : Start docker-compose with except for MAIN APP.</br>
                      - For launching the app in IDE:
                       profile = **local**

## TESTS
Includes: units and api tests. </br>
The tests can be started via Makefile. </br>
- make test-unit
- make test-api
  - but **the make up-full** must be running

## DIAGRAM
### 1) HIGH LEVEL DIAGRAM
![Alt text](./docs/diagram/high-level-diagram.png?raw=true "High level diagram")

### 2) DATABASE DIAGRAM
![Alt text](./docs/diagram/database-diagram.png?raw=true "Database diagram")

## SHARD ID GENERATOR
- How can we create IDs for rows across shards and still keep them unique and sortable?
  - In the first row i shard by country (e.g., "de" for Deutschland, "fr" for France),
    because partner in France will not sell food in other countries.
    Hence, it's unnecessary to store France's partner in the same shard as partners from Deutschland (this also applies to Products and Orders). 
    As a result, I store all related data together within the same shard.

**How do i create IDs for PARTNERS and PRODUCTS in the different shards?**
  - I suggest using 32-bit IDs for partners and products, which should be sufficient for each country.
  - These IDs would comprise the shard key and a sequence ID starting from 0 for each shard.
</br>
</br>

![Alt text](./docs/diagram/partner-and-product-id.png?raw=true "Partner and product id generator")
</br>
</br>

**How do i create IDs for ORDERS and their STATUSES in different shards?**
- Given that orders are created much more frequently every day, along with multiple statuses (four statuses per order), 32-bit IDs are insufficient.
- Therefore, I propose using 64-bit IDs, which include a timestamp, shard key, and a sequence ID.
</br>
</br>

![Alt text](./docs/diagram/orders-and-statuses-id.png?raw=true "Order and statuses id generator")
</br>
</br>
- Orders and statuses are additionally stored in tables for the current month partitioned by year and month. 
- For instance: orders_2023_10, statuses_2023_10, orders_2023_11, statuses_2023_1, and so on.
- To generate IDs for a specific shard or (orders|statuses) table for a particular month, 
  we can utilize the PLSQL functions that were created during migration.
</br>

**How can a new SHARD be added?**
  - Currently, this process is tightly coupled with the code. If we intend to add a new shard for a country, we should follow these steps. 
  1. Update the PLSQL function **getShardIndex** to include a new country index for database access.
  2. Add a new configuration class to /src/main/java/com/fooddelivery/order/configuration/datasource (both WRITE and READ).

- **What would be a better solution?** -> Implement a SERVICE DISCOVERY mechanism that can retrieve information about all available shards.

## DOMAIN
### 1) Order states
![Alt text](./docs/diagram/order-states-diagram.png?raw=true "Order states machine")

