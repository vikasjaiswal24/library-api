# Library-Management-API

### Build the service
```
git clone https://github.com/vikasjaiswal24/library-api.git
cd library-api
mvn clean install
```

### Run the service
```
java -jar library-0.0.1-SNAPSHOT.jar
OR
mvn spring-boot:run
```


### H2 console
```
Check the book details in H2 in memory database

Open the link http://localhost:5000/h2-console
confirm the JDBC URL, it should be "jdbc:h2:mem:navin"
Just click OK
```


### APIs

+ Run the library api, just hit the url below:

```
 http://localhost:5000
```






# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Handling Form Submission](https://spring.io/guides/gs/handling-form-submission/)

