# Login Process
Registered user can verify their authenticity with the login user an the correct password. After the successful authentication the authorization based on the role and their corresponding permission is granted.

![Login Process](https://git.elmar-dott.com/)

To complete the Login Process the system run several tasks:
* create a HTTP (Spring) session and a browser cookie to store the Session IDv
* Create a new login entry

## Requirements
**01 - Limited Faild Logins**: To prevent brute force attacks the number of failed logins is limited. The amount of failed tryouts is per default set to 3 an can be changed via configuration.
**02 - Session time out**: After 10 Minutes of inactivity the session got expierd and the user will be loged out. This option is configurable.
**03 - Single Login**: An user can not peform multiple logins at the same time. The typical scenario is using multiple devices like tablet and smartphone.
**04 - Batch Clean Up**: The ACL module need to fit many security concerns. Some of those risks are also based on data collection. Depending how the application will be use may government institutions wish to have user META data like they are stored in the LoginDO. This is why those data need to be erased periodically.