# GraphQL implementation in Spring Boot with Netflix's dgs-framework library

A sample spring boot application which illustrates how to use ["dgs-framework"](https://github.com/Netflix/dgs-framework) to implement graphql schemas, endpoints. 
This application uses H2 as in-memory database to showcase, but you can also use an external database instead.

### Implementation showcases

1. GraphQL Schema definitions
2. Mapping Query & Mutations to Datafetcher
3. Mapping field level Query to Datafetcher
4. Defining and Registering Dataloader and dataloader registry
5. Exception Handling

## To run this :
`` mvn clean install ``

`` mvn spring-boot:run``

`` docker-compose up --build -d``
