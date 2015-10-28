#HvA project Implementation - 2015
Currently under development<br>
This project implementation, for the University of Applied Sciences of Amsterdam/ Hogeschool van Amsterdam<br>
 <br>
  Feel free to copy/use it for your own project.<br>
  Keep in mind that it took me several days/weeks, beers and asperines to make this.<br>
  So be nice, and give me some credit, I won't bite and it won't hurt you.<br>
 <br>
Now that is said, I can explain what I've made.<br>
First of all you need to understand the different parts of this project<br>

* SAS: Smart Authentication Server (JAVA Servlet)<br>
* APS: Authentication Provider Service (LDAP directory)<br>
* ARS: Authentication Request Server (VPN server and NPS with our extension DLL)<br>
* APP: The android/ios app to enable the 2nd Authentication step<br>
<br>
A simple overview if the device is not registered in our APS:<br>
> ARS send access request over RADIUS to NPS<br>
> NPS extension receive access request<br>
> NPS extension send authentication request to SAS<br>
> SAS Authenticates the user via APS, APS returns NO userinfo<br>
> SAS reply with code 2 (User needs device registration) and send sendmail request to APS<br>
> APS sends email to user<br>
> User follows instructions mail (Download from link and open from link)<br>
> User logs in with device<br>
> Device sends register request to SAS<br>
> SAS forwards device registration request to APS<br>
> APS registers device<br>
> SAS sends result code to device<br>
<br>
When a device is already registered in our APS it goes as followed:
> ARS send access request over RADIUS to NPS<br>
> NPS extension receive access request<br>
> NPS extension send authentication request to SAS<br>
> SAS Authenticates the user via APS, APS return userInfo<br>
> SAS sends a PUSH notification to device<br>
> Device access response (approved or cancelled) is send to SAS<br>
> SAS replies to ARS with corresponding code<br>
> ARS proccesses the request (code 0= accept/ code 1=reject)<br>
<br>
You can find all the SAS error codes in the errorCode.java class<br>
