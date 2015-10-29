# HvA project Implementation - 2015
This project implementation, for the University of Applied Sciences of Amsterdam/ Hogeschool van Amsterdam.

**Feel free to copy/use it for your own project.  
Keep in mind that it took me several days/weeks, beers and asperines to make this.  
So be nice, and give me some credit, I won't bite and it won't hurt you.**  

Now that is said, I can explain what I've made.
First of all you need to understand the different parts of this project.
There are 4 different parts
- SAS: Smart Authentication Server (JAVA Servlet)
- APS: Authentication Provider Service (LDAP directory)
- ARS: Authentication Request Server (VPN server and NPS with our extension DLL)
- APP: The android/ios app to enable the 2nd Authentication step

Each part has its own folder in this repo. (Except for SAS and APS, same servlet).  
There are 2 scenarios, 1 where the user has no device registered and 2 the case where there *is* a device registered.
### Scenario 1
A simple overview if the device is not registered in our APS:

1. ARS send access request over RADIUS to NPS
2. NPS extension receive access request
3. NPS extension send authentication request to SAS
4. SAS Authenticates the user via APS, APS returns NO userinfo
5. SAS reply with code 2 (User needs device registration) and send sendmail request to APS
6. APS sends email to user
7. User follows instructions mail (Download from link and open from link)
8. User logs in with device
9. Device sends register request to SAS
10. SAS forwards device registration request to APS
11. APS registers device
12. SAS sends result code to device
When this is done the user retry to login and will automaticly follow scenario 2


### Scenarion 2
When a device is already registered in our APS it goes as followed:

1. ARS send access request over RADIUS to NPS
2. NPS extension receive access request
3. NPS extension send authentication request to SAS
4. SAS Authenticates the user via APS, APS return userInfo
5. SAS sends a PUSH notification to device
6. Device access response (approved or cancelled) is send to SAS
7. SAS replies to ARS with corresponding code
8. ARS proccesses the request (code 0= accept/ code 1=reject)
9. YEAAAH, user just logged in!!! (If we have chosen grand access ofcourse)

You can find all the SAS error codes in the ErrorCode.java class
