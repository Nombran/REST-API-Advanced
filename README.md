# REST API Advanced

__This module is an extension of the RESTful web-service from Module #2 REST API Basics.__

During your work on this module make sure you adhere to the next requirements:
1. Code should be clean and should not contain any “developer-purpose” constructions.
2. Application should be designed and written with respect to OOD and SOLID principles.
3. Code should contain valuable comments where appropriate.
4. Public APIs should be documented using Javadoc.
5. A clear layered structure should be used: responsibilities of each application layer should be defined.
6. JSON should be used as a format of client-server communication messages.
7. Abstraction should be used to avoid code duplication.
8. Convenient error/exception handling mechanism should be implemented: all errors should be meaningful.

Application requirements are the following:
1. JDK version: 8. Use Streams, java.time.*, an etc. where it is appropriate.
2. Application packages root: com.epam.esm.
3. Java Code Convention is mandatory (exception: margin size –120 characters).
4. Apache Maven/Gradle. Multi-module project.
5. Spring Framework 5.+.
6. Database: PostgreSQL 9.+ or 10.+.
7. Testing: JUnit 4.+ or 5.+, Mockito.
8. Service layer should be covered with unit tests not less than 80%.

## Sub-module #1 - Spring Boot
Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications that you can "just run". 
In this module you will learn how to create RESTful web service using Spring Boot. 

### Task #1
1. Migrate your existing Spring application from a previous module to a Spring Boot application.

## Sub-module #2 - REST API
This sub-module is an extension of REST API Basic and it covers such topics as pagination, sorting, filtering and HATEOAS.

Please imagine that your application has a lot of data, so when you make a GET request it will return, for instance, 1 million records. 
This will take much time to process such request and return the result to the consumer of your API. 
That is exactly what pagination, sorting, and filtering can solve.

The other topic is HATEOAS what stands for the phrase "Hypermedia As The Engine Of Application State". 
When you are viewing a web page, you see data on it and can perform some actions with this data. 
In REST when you request a resource you get the details of the resource in the response. 
Along with it you can send the operations that you can perform on the resource. 
And this is what HATEOAS does.

### Task #2
The application should be extended to expose the following REST APIs:
1. Change single field of main entity (e.g. if you are using suggested in the previous module model, you should implement the possibility to change only duration of a service or only price).
2. Make an order (or any relevant action) on main entity for a user.
3. Get information about user’s orders.
4. Get information about user’s order: cost and timestamp of a purchase.
5. Get the most widely used secondary entity of a user with the highest cost of all orders. 
    * Demonstrate SQL execution plan for this query.
6. Search main entity by several secondary entities (“and” condition).
7. Pagination should be implemented for all GET-all endpoints. Please create a flexible and non-erroneous solution. Handle all exceptional cases.
8. Support HATEOAS on REST endpoints.
9. For demo, generate at least:
    * 1000 users;
    * 1000 secondary entities;
    * 10 000 main entities;
    * all entities should be linked, all values should look meaningful: random words, but not random letters. 
10. APIs should be demonstrated using Postman tool. 
11. For demo, prepare Postman collections with APIs.

## Sub-module #3 - ORM & JPA

This sub-module covers following topics:
1. ORM
2. JPA & Hibernate
3. Transactions

ORM stands for Object Relational Mapping. It’s a bit of an abstract concept – but basically it’s a technique that allows us to query and change data from the database in an object oriented way. 
ORMs provide a high-level abstraction upon a relational database that allows a developer to write Java code instead of SQL to create, read, update and delete data and schemas in their database. 
Developers can use the programming language they are comfortable with to work with a database instead of writing SQL statements or stored procedures.

A JPA (Java Persistence API) is a specification of Java which is used to access, manage, and persist data between Java object and relational database. It is considered as a standard approach for Object Relational Mapping.
JPA can be seen as a bridge between object-oriented domain models and relational database systems. Being a specification, JPA doesn't perform any operation by itself. Thus, it requires implementation. So, ORM tools like Hibernate, TopLink, and iBatis implements JPA specifications for data persistence.

A transaction usually means a sequence of information exchange and related work (such as database updating) that is treated as a unit for the purposes of satisfying a request and for ensuring database integrity. 
For a transaction to be completed and database changes to made permanent, a transaction has to be completed in its entirety.

### Task #3

1. Hibernate should be used as a JPA implementation for data access.
2. Spring Transaction should be used in all necessary areas of the application.
3. Audit data should be populated using JPA features (an example can be found in materials).

Note: it is forbidden to use any Hibernate specific features.

## Sub-module #4 - Authentication

This sub-module is an extension of REST API Basic and covers following topics:
1. Spring Security framework
2. Oauth2 and OpenId Connect
3. JWT token

Spring Security is a powerful and highly customizable authentication and access-control framework.
It is the de-facto standard for securing Spring-based applications.

OAuth 2.0 is a security standard where you give one application permission to access your data in another application.
The steps to grant permission, or consent, are often referred to as authorization or even delegated authorization.
You authorize one application to access your data, or use features in another application on your behalf, without giving them your password.

OpenID Connect (OIDC) is a thin layer that sits on top of OAuth 2.0 that adds login and profile information about the person who is logged in.

JSON Web Tokens are JSON objects used to send information between parties in a compact and secure manner.

### Task #4
1.Spring Security should be used as a security framework.

2.Application should support only stateless user authentication and verify integrity of JWT token.
Users should be stored in a database with some basic information and a password.

User Permissions:

2.1. Guest:
   * Read operations for main entity.
   * Signup.
   * Login.
   
2.2. User:
   * Make an order on main entity.
   * All read operations.

2.3. Administrator (can be added only via database call):
   * All operations, including addition and modification of entities.

3.Get acquainted with the concepts Oauth2 and OpenId Connect

### Optional Task
1. Use Oauth2 as an authorization protocol.
    * OAuth2 scopes should be used to restrict data.
    * Implicit grant and Resource owner credentials grant should be implemented.

