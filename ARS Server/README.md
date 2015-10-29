# Extension DLL for the ARS server
The ars exists of 2 types:

1. VPN server, running RRAS
2. RADIUS server, running NPS

When the user tries to connect through VPN, the credentials are send by a RADIUS packet to the RADIUS server.  
On the RADIUS server is an extra extension loaded to make sure the 2nd authentication step is used. (Using the app).  
The RADIUS server responds within 30 seconds with the answer to the VPN server (Access granded or denied).

## VPN server (PPTP)
This server is partitialy open the the internet. One port is configured in a DMZ zone, others are connected the the secure company network.  
The VPN server has the following configurations:

- Use RADIUS for accounting and authentication
- Connect to the RADIUS server
- RADIUS packet timeout is set to 40 seconds (!!IMPORTANT!!)
- Registered to the company domain

## NPS server (RADIUS)
On this server the actual authentication is done.  
First the server checks if the provided credentials are correct.  
If correct, the extension will start.  
*The extension ALWAYS returns an answer (accept or reject)*  
The extension can responde is 3 ways:

1. Accept
2. Reject
3. Discard (Not actualy responding)

The extension is configured to NEVER answer with a discard message, as the NPS doens't forward the discard message back to the VPN server.  
This will be seen as a timeout by the VPN server, so it tries agian after 40 seconds.  
By that time most devices (computer trying to connect to VPN) automaticaly hang up because they timed out.  
Thats why the extension ALWAYS responds with a reject message, except when the user granded access ofcourse.  
Here a list of possible scenarios with can occur to the extension:

- User granded access                       ==> Access accept
- User denied access                        ==> Access reject
- User timed out (no response in 30 sec.)   ==> Access reject
- SAS server error (HTTP 500, 404, etc.)    ==> Access reject
- SAS responds with other code than 0       ==> Access reject
- SAS responds with no content body         ==> Access reject
- SAS responds with invalid JSON body       ==> Access reject

The Extension also logs the activity to a log file. The directory is hard coded to ```C:\Temp\NPS.log.txt```.  
It logs:

- New connection request and with what credentials and IP address
- POST to SAS FAILED, with what error code
- POST to SAS succeeded with a valid response, also what the response code was (Provided by SAS)

This allows the system administrator to see if there is an hacker trying to connect and to determine if the hacker must be banned.
