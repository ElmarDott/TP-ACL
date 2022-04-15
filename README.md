<img src="https://elmar-dott.com/wp-content/uploads/ElmarDott.com_.jpg" style="float:left; height:50%; width:50%;" />

# Together Platform :: ACL

[![License Apache 2](https://img.shields.io/github/license/ElmarDott/TP-CORE)](https://www.apache.org/licenses/LICENSE-2.0)
[![Maven Central](https://img.shields.io/badge/Maven%20Central-2.0.2-green.svg)](https://mvnrepository.com/artifact/io.github.together.modules/core)
[![Javadocs](https://www.javadoc.io/badge/io.github.together.modules/core.svg)](https://www.javadoc.io/doc/io.github.together.modules/core)
[![Build Status](https://travis-ci.org/ElmarDott/TP-CORE.svg?branch=master)](https://travis-ci.org/ElmarDott/TP-CORE)
[![Coverage Status](https://coveralls.io/repos/github/ElmarDott/TP-CORE/badge.svg?branch=master)](https://coveralls.io/github/ElmarDott/TP-CORE)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/f00b311bb51247c1ac215b699b52e5ed)](https://app.codacy.com/app/ElmarDott/TP-CORE?utm_source=github.com&utm_medium=referral&utm_content=ElmarDott/TP-CORE&utm_campaign=Badge_Grade_Dashboard)

## Getting Started
Access Control List. Functions - Release: 1.0

 * [ACL-01] Configure Module
 * [ACL-02] Registration
 * [ACL-03] Login / Logout
 * [ACL-04] Reset Password

Please also check out the [Wiki](https://github.com/ElmarDott/TP-ACL/wiki/home) for further information.

### Prerequisites

The ACL Module is build with NetBeans, Maven and Java SE (openJDK). The most important dependencies are Hibernate, Spring and JUnit 5. As Database Server (DBMS) PostgeSQL DBMS 11 is recommended.

Docker was chosen for an simple and fast database setup. In the case you wish to have a short introduction about docker, you can check my tutorial on [BitCute](https://elmar-dott.com/articles/tutorial/docker-basics/). After on your system docker is running, you are be able to setup the database by the following steps:

  docker network create -d bridge --subnet=172.18.0.0/16 services

  docker run -d --name postgres --restart=no \
  -p 5432:5432 --net services --ip 172.18.0.2 \
  -e POSTGRES_PASSWORD=s3cr3t \
  -e PGPASSWORD=s3cr3t \
  -v /home/user/docker/postgres:/var/lib/postgresql/data \
  postgres:11

  docker run -d --name pgadmin --restart=no \
  -p 8004:80 --net services --ip 172.18.0.98 \
  -e PGADMIN_DEFAULT_EMAIL=myself@sample.com \
  -e PGADMIN_DEFAULT_PASSWORD=s3cr3t \
  --link postgres:11 \
  dpage/pgadmin4:4.29

  **URI**/>  172.17.0.1:5432   User: postgres PWD: n/a
  **DOC**/>  https://docs.docker.com/samples/library/postgres/

  * docker start postgres
  * docker stop postgres

To create default user and schemata (also for testing), you are be able to use [TP-CM/dbms/src/sql/initial_postgresql.sql](https://github.com/ElmarDott/TP-CM/blob/master/dbms/src/sql/initial_postgresql.sql) script. 

### Build

TP-ACL uses always the current version of Apache Maven. To build the project by your own you will need the current version from the master branch of the parent-pom from the TP-CM project (build-workflow).

TP-ACL depends on TP-CORE.

The project configurations are available in src/main/filter/ directory.

In the case no DBMS is available, all test cases which depend on Database access will skipped.


### Installing
All released artifacts are available on Maven Central for free usage. You are be able to use the released artifact in your project as dependency with the following entry:

**Maven**
```
<dependency>
    <groupId>io.github.together.modules.acl</groupId>
    <artifactId>api</artifactId>
    <version>1.0.0</version>
</dependency>
<dependency>
    <groupId>io.github.together.modules.acl</groupId>
    <artifactId>server</artifactId>
    <version>1.0.0</version>
</dependency>
<dependency>
    <groupId>io.github.together.modules.acl</groupId>
    <artifactId>client</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Authors

* **Elmar Dott** - [*Concept, Architecture, Development*](https://elmar-dott.com)

## License

This project is licensed under the Apache 2.0 license.

## Contributors

Feel free open a pull request or to send a feature request by e-mail in the case you want to contribute the project. Everyone is welcome, even beginners in programming. I also appreciate help by optimizing the documentation and creating tutorials.

Mistakes happen. But we only able to fix them, when we you inform us you found a bug. Do not hesitate to send a report in the way you feel common. I try to give as much as possible fast & direct support.

In the case you like this project, let me know it and rate it with a star.

## Release Notes

|Version | Comment
|--------|----------------------------------------------------------------------
| 1.0    | in process
|        | - Add Functionality: Domain Model
