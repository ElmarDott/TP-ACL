# Introduction

This is a brief introduction for the functionality of the TP-ACL module. An Open Source an free usage project, also for commercial purpose under Apache License 2.

TP-ACL is a library divided into 3 modules. The main modul API is a shared dependency for the modules SERVER & CLIENT. This architecture allows an independent deployment for SERVER and CLIENT. Alle 3 submodules will always released together.
```xml
    <dependency>
        <groupId>io.github.together.modules.acl</groupId>
        <artifactId>api</artifactId>
        <version>${version}</version>
    </dependency>

    <dependency>
        <groupId>io.github.together.modules.acl</groupId>
        <artifactId>server</artifactId>
        <version>${version}</version>
    </dependency>

    <dependency>
        <groupId>io.github.together.modules.acl</groupId>
        <artifactId>client</artifactId>
        <version>${version}</version>
    </dependency>
```

All released artifacts are deployed to Maven Central. [Here](https://search.maven.org/#search%7Cga%7C1%7Cio.github.together) you will find an overview of available versions and components. Each deployed Release is tagged in the SCM.

# Kick Start: Build & modify the last revision from the SCM
To run an own build in a local development environment, its necessary to follow some simple prerequisite:

- checkout source repository [TP-CM](https://git.elmar-dott.com/scm/repo/TogetherPlatform/TP-CM) and build it
- have an [PostgreSQL](https://elmar-dott.com/tutorial/postgres-docker-container/) DBMS installation
- checkout source repository [TP-ACL](https://git.elmar-dott.com/scm/repo/TogetherPlatform/TP-ACL/) and build it.

The together platform use the Maven filter technology for token replacement. The specific property files **[database](https://github.com/ElmarDott/TP-CORE/blob/master/src/main/filters/database.properties)** in the /src/main/filter directory holds configuration parameters which are changeable during the build. The necessity of a change is only for configure an own workspace to run your own builds.

# Exceptions

The module contains a set of custom Exceptions, which defined for the implemented services. The following list gives an overview which Exception is available:

* [AuthetificationException](https://git.elmar-dott.com/scm/repo/TogetherPlatform/TP-ACL/code/sources/develop/api/src/main/java/org/europa/together/exceptions/acl/AuthenticationException.java/)
* [AuthorisationException](https://git.elmar-dott.com/scm/repo/TogetherPlatform/TP-ACL/code/sources/develop/api/src/main/java/org/europa/together/exceptions/acl/AuthorisationException.java/)

# Utils
org.europa.together.utils.**[Constraints](https://git.elmar-dott.com/scm/repo/TogetherPlatform/TP-ACL/code/sources/develop/api/src/main/java/org/europa/together/utils/acl/Constraints.java/)**:  is a collection of helpful system wide constants.

# Architectural overview

All TP applications following an internal layer architecture, were each layer is represented by a own package. The Access between the layers is from TOP to DOWN. That means the lowest prioritized Layer, the Domain Layer can accessed from all layers above. itself has the no other layers then himself. Technically it means that Domain Objects has no import from other layers. The image below demonstrate the used layer architecture.

![Layer Architecture](https://git.elmar-dott.com/scm/api/v2/plugins/directFileLink/TogetherPlatform/TP-ACL/src%2Fsite%2Fresources%2Fimages%2Fdoc%2FLayerArchitecture.png)



