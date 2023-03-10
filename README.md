# spring-security-jpa

The project aims at reducing the boilerplate code necessary to build a new Spring Project using both Spring Security and Spring Data JPA.

As Spring Security has built-in support for JDBC, but not for Spring Data JPA, every time a new project starts using both the technologies, it is necessary to run the SQL script stored in JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION against the used Database, optionally adapting it to the Dialect used in the project.

