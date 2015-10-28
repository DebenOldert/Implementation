# HvA project Implementation - 2015

Currently under development.
This project implementation, for the University of Applied Sciences of Amsterdam/ Hogeschool van Amsterdam.

- Feel free to copy/use it for your own project.
- Keep in mind that it took me several days/weeks, beers and asperines to make this.
- So be nice, and give me some credit, I won't bite and it won't hurt you.

Now that is said, I can explain what I've made.
First of all you need to understand the different parts of this project.
There are 4 different parts
- SAS: Smart Authentication Server (JAVA Servlet)
- APS: Authentication Provider Service (LDAP directory)
- ARS: Authentication Request Server (VPN server and NPS with our extension DLL)
- APP: The android/ios app to enable the 2nd Authentication step

Each part has its own folder in this repo. (Except for SAS and APS, same servlet).\
There are 2 scenarios, 1 where the user has no device registered and 2 the case where there *is* a device registered.
### Scenario 1
A simple overview if the device is not registered in our APS:
> ARS send access request over RADIUS to NPS\
> NPS extension receive access request\
> NPS extension send authentication request to SAS\
> SAS Authenticates the user via APS, APS returns NO userinfo\
> SAS reply with code 2 (User needs device registration) and send sendmail request to APS\
> APS sends email to user\
> User follows instructions mail (Download from link and open from link)\
> User logs in with device\
> Device sends register request to SAS\
> SAS forwards device registration request to APS\
> APS registers device\
> SAS sends result code to device
When this is done the user retry to login and will automaticly follow scenario 2


### Scenarion 2
When a device is already registered in our APS it goes as followed:
> ARS send access request over RADIUS to NPS
> NPS extension receive access request
> NPS extension send authentication request to SAS
> SAS Authenticates the user via APS, APS return userInfo
> SAS sends a PUSH notification to device
> Device access response (approved or cancelled) is send to SAS
> SAS replies to ARS with corresponding code
> ARS proccesses the request (code 0= accept/ code 1=reject)

You can find all the SAS error codes in the ErrorCode.java class
