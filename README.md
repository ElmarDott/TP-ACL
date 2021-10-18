<img src="https://enrebaja.files.wordpress.com/2018/04/logo_250x250.png" style="float:left; height:50%; width:50%;" />

# together Platform :: ACL

[![License Apache 2](https://img.shields.io/github/license/ElmarDott/TP-CORE)](https://www.apache.org/licenses/LICENSE-2.0)
[![Maven Central](https://img.shields.io/badge/Maven%20Central-2.0.2-green.svg)](https://mvnrepository.com/artifact/io.github.together.modules/core)
[![Javadocs](https://www.javadoc.io/badge/io.github.together.modules/core.svg)](https://www.javadoc.io/doc/io.github.together.modules/core)
[![Build Status](https://travis-ci.org/ElmarDott/TP-CORE.svg?branch=master)](https://travis-ci.org/ElmarDott/TP-CORE)
[![Coverage Status](https://coveralls.io/repos/github/ElmarDott/TP-CORE/badge.svg?branch=master)](https://coveralls.io/github/ElmarDott/TP-CORE)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/f00b311bb51247c1ac215b699b52e5ed)](https://app.codacy.com/app/ElmarDott/TP-CORE?utm_source=github.com&utm_medium=referral&utm_content=ElmarDott/TP-CORE&utm_campaign=Badge_Grade_Dashboard)

The ACL Artifact is a implementation of an Access Control List.

## Getting Started

Components - Release: 1.0
 * [ACL-01] Domain Objects (DO)
 * [ACL-02] Data Access Objects (DAO)

Basic concepts of this project are: KISS (Keep it simple, stupid), COC (Convention
over configurations) and DRY (Don't repeat yourself). Also we following the programming
paradigms of: Test Driven Development (TDD), Behavioral Driven Development (BDD)
and Domain Driven Development (DDD).

### Prerequisites

The CORE Module is build with NetBeans 12.3, Maven 3.6.3 and Java 11 SE (openJDK).
The implementation is also designed to run in Java EE 9 (e.g. Tomcat) environments.
The most important dependencies are Hibernate 5.4, Spring 5.3 and JUnit 5. As
Database Server (DBMS) we recommend PostgeSQL DBMS 11.

We decided to use docker for an easy database setup. After on your system docker
is running you are be able to setup the database by the following steps:

  docker network create -d bridge --subnet=172.18.0.0/16 services

  docker run -d --name postgres --restart=no \
  -p 5432:5432 --net services --ip 172.18.0.2 \
  -e POSTGRES_PASSWORD=s3cr3t \
  -e PGPASSWORD=s3cr3t \
  -v /home/user/docker/postgres:/var/lib/postgresql/data \
  postgres:11

  docker run -d --name pgadmin --restart=no \
  -p 8004:80 --net services --ip 172.18.0.98 \
  -e PGADMIN_DEFAULT_EMAIL=elmar.dott@gmail.com \
  -e PGADMIN_DEFAULT_PASSWORD=s3cr3t \
  --link postgres:11 \
  dpage/pgadmin4:4.29

  URI/>  172.17.0.1:5432   User: postgres PWD: n/a
  DOC/>  https://docs.docker.com/samples/library/postgres/

  * docker start postgres
  * docker stop postgres

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
    <version>1.0</version>
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
