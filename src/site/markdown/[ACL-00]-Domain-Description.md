# Domain Description
The TP-ACL domain is setup by 5 entities: [Login](https://git.elmar-dott.com/scm/repo/TogetherPlatform/TP-ACL/code/sources/develop/api/src/main/java/org/europa/together/domain/acl/LoginDO.java/), [Account](https://git.elmar-dott.com/scm/repo/TogetherPlatform/TP-ACL/code/sources/develop/api/src/main/java/org/europa/together/domain/acl/AccountDO.java/), [Role](https://git.elmar-dott.com/scm/repo/TogetherPlatform/TP-ACL/code/sources/develop/api/src/main/java/org/europa/together/domain/acl/RolesDO.java/), [Permission](https://git.elmar-dott.com/scm/repo/TogetherPlatform/TP-ACL/code/sources/develop/api/src/main/java/org/europa/together/domain/acl/PermissionDO.java/) & [Resource](https://git.elmar-dott.com/scm/repo/TogetherPlatform/TP-ACL/code/sources/develop/api/src/main/java/org/europa/together/domain/acl/ResourcesDO.java/). All Domain Objects (DO) are locate in the module API inside the package domain. The database tables will be create via Hibernate (JPA) by using the DO. The prefix for the domain tables is acl.

## ERM Diagram
![ERM Diagram](https://git.elmar-dott.com/scm/api/v2/plugins/directFileLink/TogetherPlatform/TP-ACL/src%2Fsite%2Fresources%2Fimages%2Fdoc%2FERM%20ACL.png)

## Description
* Every Permission has exact one Resource
* An Role can have multiply Permissions
* An Account has exact one Role
* An Account has multiple Logins

## Processes
* Registration
* Login / Logout
* Change / Reset Password
* Update Module Configuration
* Activate / Deactivate Accounts