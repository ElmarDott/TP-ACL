# together Platform :: ACL

The ACL Artifact is a implementation of an Access Control List.

## Getting Started

Components - Release: 1.0
 * [ACL-001] Domain Objects (DO)
 * [ACL-002] Data Access Objects (DAO)
 * [ACL-003] Services (RESTful)

Basic concepts of this project are: KISS (Keep it simple, stupid), COC (Convention
over configurations) and DRY (Don't repeat yourself). Also we following the programming
paradigms of: Test Driven Development (TDD), Behavioral Driven Development (BDD)
and Domain Driven Development (DDD).

### Prerequisites

The ACL Module is build with NetBeans 11.3, Maven 3.6.1 and Java 11 SE. The
implementation is also designed to run in Java EE 8 (e.g. Tomcat) environments.
The most important dependencies are Hibernate 5.3, Spring 5.1 and JUnit 5. As
Database Server (DBMS) we recommend PostgeSQL DBMS 11.

We decided to use docker for an easy database setup. After on your system docker
is running you are be able to setup the database by the following steps:

  * docker pull postgres
  * docker run -d -p 5432:5432 --name postgres -v /home/user/docker/postgreSQL:/var/lib/postgresql/data postgres
  * docker start postgres
  * docker stop postgres

  URI/>  172.17.0.1:5432   User: postgres PWD: n/a
  DOC/>  https://docs.docker.com/samples/library/postgres/

To create user and schemata (also for testing), you are be able to use TP-CM/dbms/src/sql/initial_postgresql.sql
script. If you need a short introduction about docker, you can check our tutorial on [YouTube](https://www.youtube.com/channel/UCBdJ0zh8xnMrQ-xQ4Gymy2Q).

### Build

To build the Project you will need the parent-pom from the TP-CM project
(build-workflow). The project configurations are available in src/main/filter/
directory.

In the case there is no DBMS available, all test cases which depend on Database
access will skipped.

### Installing
All released Artifacts will be available on Maven Central for usage. To fit with the
hosting restriction on Sonatype Open Source Project Repository Hosting, it was necessary
to change the POM GAV. As Result the Java packages do not fit with the pom GAV. So you
are be able to use the artifact in your project as dependency with the following entry:

Please check the Release Notes for published Artifact Versions.
**Maven**

```
<dependency>
    <groupId>io.github.together.modules</groupId>
    <artifactId>acl</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Authors

* **Elmar Dott** - *Concept, Architecture, Development* - [enRebaja](https://enRebaja.wordpress.com)

## License

This project is licensed under the Apache 2.0 license.

## Contributors

Feel free to send a request by e-mail in the case you want to contribute the
project. Everyone is welcome, even beginners in programming. We also appreciate
help by optimizing our documentation and creating tutorials.

Mistakes happen. But we only able to fix them, when we you inform us you find a
bug. Do not hesitate to send a report in the way you feel common. We try to give
as much as possible fast & direct support.

In the case you like this project, let me know it and rate it with a star.

## Release Notes

|Version | Comment
|--------|----------------------------------------------------------------------
| 1.0    | in process
|        | - Add Functionality: Domain Model
|        | - Add Functionality: Domain Access Objects



