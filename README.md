Application testing playground
===============================

Used:

* Spring Boot 1.4
* AssertJ
* Awaitability


### Commands

`mvn test` - compiles and executes only unit tests.
`mvn verify` - packages and executes unit tests and integration tests.
`mvn package` - packages without intergration tests 
`mvn package -DskipTests` - packages without all tests