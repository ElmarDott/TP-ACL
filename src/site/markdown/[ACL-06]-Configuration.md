# administrate Configuration
The module configuration is based on the [ConfigurationDAO](https://together-platform.org/tp-core/[CORE-05]-ConfigurationDAO.html).

module-name: acl, configuration-set: conf

* **password.pattern** Regular Expression default:[A-Za-z0-9]{10,20}
* **session.timeout** automatic logout ater inactivity - s= seconds, m= minutes, h=hours default(15s)
* **pepperized** global String to peperize the server instance default(#pepperized#)
* **iam.server** URL to the Identy Access Manager e.g. KeyCloak
* **iam.realm** e. g. myCompany
