# Microservices Project using Spring Boot, and Spring Cloud technologies.

User can place an order if there is sufficient quantity available in stock in inventory.
Upon successful order placement, the notification from Order Service is sent out to Notification Service. In order to place an order, user needs to be authorized. All services are secured.

### Build Microservices :

Order Service, Inventory Service, Notification Service, Api Gateway, Discovery server

### Routing :

Api gateway

### Security :

Keycloak

### Discovery Server :

Netflix Eureka

### Fault Tolerance :

Circuit Breaker, Timeout, Retry, Actuator

### Streaming :

Kafka broker, zookeeper

### Distributed Tracing :

Micrometer, Zipkin (UI)

### Monitoring :

Prometheus, Grafana

### Databases :

MySQL
Postgres

### Editor :

IntelliJ

### Libraries:

Resilience4J
Jib

### Programming Language :

Java 8

### OS:

MacOS

### API Testing :

Postman

## Topics covered :

1. Inter-process communication
2. Service Discovery using Netflix Eureka
3. Implement API Gateway using Spring Cloud Gateway - Load Balancing and Routing
4. Secure Microservices using Keycloak
5. Implement Circuit Breaker, Timeout, Retries
6. Implement Distributed Tracing
7. Event-Driven Architecture using Kafka
8. Dockerized the application
9. How to create docker images for your modules without using docker - Jib library
10. Monitoring Microservices using Prometheus and Grafana


## Common commands used : 

1. docker - to build images, download images
2. docker-compose : to download images & run container
3. mvn - clean, build, install
4. jib - build, dockerBuild
5. git - init, status, pull, add, commit, push




