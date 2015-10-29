# SAS/APS server
The SAS and APS server are both running a JAVA servlet. I combined them in one .war file.  
The servers ALWAYS respond with a JSON answer and ONLY accept a POST JSON request.  

## SAS
The SAS server is open to the internet because user must be able to send there connection response to the server.  
The servet takes care of the following:

- Disallow GET requests
- Validate incoming POST JSON
- Forwards Authentication request to APS
  - If response contains user info then a PUSH notification can be send to the registerd device
  - If response does not contain user info then send an email with the steps to install and register the app/device
- Forwards register request to APS and forward APS response to device
- Forward unregister request to APS
- Handles the confirm request from the device (approved, cancelled)

## APS
The APS server validates the user credentials and is the connection to the LDAP directory.  
The server handles the following:

- Disallow GET requests
- Validate incoming POST JSON
- Answers authentication requests
- Sends email messages
- Registers devices in the LDAP directory
- Unregisters devices in the LDAP directory
