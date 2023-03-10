# Spring Security JPA

The project aims at reducing the boilerplate code necessary to build a new Spring Project using both Spring Security
and Spring Data JPA.

## The problem

As Spring Security has built-in support for *JDBC*, but not for *Spring Data JPA*, every time a new project starts
using both the technologies, it is necessary to run the SQL script stored in 

        JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION

against the used Database, optionally adapting it to the *Dialect* used by the Database in the project.

## With Spring Security JPA

Using Spring Security JPA, it is possible to extend:
- JpaUser
- JpaUserRepository
- JpaUserService
- JpaAuthority
- JpaAuthorityRepository
- JpaAuthorityService

in your project, so that Entities, Repositories and Services will be included in the ComponentScan.

Alternatively, it is possible to include the package *dev.graffa.springsecurityjpa* in your ComponentScan, but this 
would give you less flexibility.

### What you get

- The new tables will be created in the Database
- The entities can be extended to adapt and integrate into your data model
- Services will fire up
- An *AuthenticationManager* Bean will be put into the Context, providing support for authentication via Jakarta 
  Persistence Entities


### Prerequisites

- A *Password Encoder* Bean in the Context, for example a *BCryptPasswordEncoder*.