# together Platform :: ACL

The CORE Artifact contains typical basic functions for Java Applications. The
Module is elaborated as library and packed as JAR file. It is possible to use
this Artifact in Java EE and Java SE (Desktop) Applications. The implementation
of these library has the goal, to create an useful and compact toolbox.

## Getting Started

Components - Release: 1.0
 * [ACL-001] Domain Objects (DO)
 * [ACL-002] Data Access Objects (DAO)
 * [ACL-003] Services (RESTful)
 * [ACL-004] JSF UI

### Prerequisites

The CORE Module is build with NetBeans 8.2, Maven 3.5.3 and Java 8 SE. The
implementation is also designed to run in Java EE 7 (e.g. Tomcat) environments.
The most important dependencies are Hibernate 4.3, Spring 5 and JUnit 5. As
Database Server (DBMS) we recommend PostgeSQL.

### Build

To build the Project you will need the parent-pom from the TP-CM project. The
project configurations are available in src/main/filter/ directory.

In the case there is no DBMS available, all test cases which depend on Database
access will skipped.

### Installing
All released Artifacts will be available on Maven Central for usage. To fit with the
hosting restriction on Sonatype Open Source Project Repository Hosting was necessary
to change the POM GAV. As Result the java packages do not fit with the pom GAV. SO you
are be able to use TP-CORE in your project as dependency wit the following entry:

Please check the Release Notes for published Artifact Versions.
**Maven**
```
<dependency>
    <groupId>io.github.together.modules</groupId>
    <artifactId>core</artifactId>
    <version>1.1.0</version>
</dependency>
```

## Authors

* **Elmar Dott** - *Concept, Architecture, Development* - [enRebaja](https://enRebaja.wordpress.com)

## License

This project is licensed under the General Public License GPL-3.0

## Contributors

Feel free to send a request by e-mail to contribute the project. In the case you
like this project, let me know it an rate it with a star.

## Release Notes

|Version | Comment
|--------|----------------------------------------------------------------------
| 1.0    | Rejected : not published
|--------|----------------------------------------------------------------------
| 1.0.1  | Rejected : not published
|        | - change Maven POM GAV for Open Source Project Repository Hosting
|--------|----------------------------------------------------------------------
| 1.0.2  | published 04/2018
|        | - include PGP signing for all
|--------|----------------------------------------------------------------------
| 1.1    | in process
|        | - optimize overall Test Coverage
|        | - Fix (medium) :: MailClient.loadConfigFromDatabase() {MODUL_VERSION}
|        | - Add Functionality: XmlTools:transformXslt()
|        | - Add Functionality: XmlTools:shrinkXml()
|        | - REFRACTOR XmlToolsImpl
|        | - Add Functionality: PdFRenderer.removePage()
|        | - Add Functionality: Logger.setLogLevel()
|        | - Add Functionality: DatabaseActions.getMetaData
|        | - Migrate DAO to Hibernate 5.3 (JPA 2.1)
|        | - Add swagger support



