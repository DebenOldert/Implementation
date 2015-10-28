/*
 * Feel free to copy/use it for your own project.
 * Keep in mind that it took me several days/weeks, beers and asperines to make this.
 * So be nice, and give me some credit, I won't bite and it won't hurt you.
 */
// Author: Deben Oldert

//Set includes
#include <iostream>
#include <fstream>

#include <Windows.h>
#include <Authif.h>
#include <stdio.h>
#include <stdlib.h>
#include <ctime>
#include <string>
#include "WinHttpClient.h"
#include <rapidjson\document.h>
#include "stdafx.h"

//#define _CRT_SECURE_NO_WARNINGS
void logger(const char * msg) //Logger function ==> Write to a log file
{
	FILE * fp = fopen("C:/Temp/NPS.log.txt", "a");
	if (fp)
	{
		fprintf(fp, "%s\n", msg);
		fclose(fp);
	}
}

DWORD WINAPI RadiusExtensionProcess2(
	_Inout_  PRADIUS_EXTENSION_CONTROL_BLOCK pECB //The famous function
	)
{
	if (pECB != NULL) //Check if we got some data from NPS
	{
		if (pECB->rcRequestType == rcAccessRequest) //Check if the request is a access request
		{
			RADIUS_ATTRIBUTE_ARRAY * pAR = pECB->GetRequest(pECB); //Get the attribute array
			if (pAR != NULL) //Check if the attribute array is not empty
			{
				//Initialize some variables
				char * username = "";
				char * password = "";
				char * uniqueId = "";
				char * clientIp = "";

				//Get the size of the attribute array
				DWORD size = pAR->GetSize(pAR);

				//Pre-init the attribute pointer
				const RADIUS_ATTRIBUTE * pRA;

				//Loop through each object in attribute array
				for (DWORD iAR = 0; iAR < size; iAR++)
				{
					//Get the attribute from the array
					pRA = pAR->AttributeAt(pAR, iAR);
					if (pRA != NULL) //Check if the attribute is not empty
					{
						if(pRA->dwAttrType == ratUserName) //Get the username
						{
							username = (char *) pRA->lpValue;
						}
						if(pRA->dwAttrType == ratUniqueId) //Get the request ID
						{
							uniqueId = (char *) pRA->dwValue;
						}
						if(pRA->dwAttrType == ratUserPassword) //Get the password (can be empty or "")
						{
							password = (char *) pRA->lpValue;
						}
						if(pRA->dwAttrType == ratCallingStationId) //Get the source ip address
						{
							clientIp = (char *) pRA->dwValue;
						}
					}
				}

				//Write the result to the log file
				char buft[2048];
				sprintf(buft, "Recieved Request (ID:%d) for user: %s, pass: %s",uniqueId, username, password);
				logger(buft);

				//Init a post request
				WinHttpClient post(L"http://192.168.2.240:8080/Implementation/SAS");

				//Create the request json string
				char dat[2000];
				sprintf(dat, "{\"function\":\"authenticate\",\"requestId\":%d,\"username\":\"%s\",\"password\":\"%s\"}", uniqueId, username, password);
				
				//Cast from char to string
				string data = string(dat);

				//Set request data (send json)
				post.SetAdditionalDataToSend((BYTE *)data.c_str(), (int) data.size());

				//Set request headers
				wchar_t szSize[50] = L"";
				swprintf_s(szSize, L"%d", data.size());
				wstring headers = L"Content-Length: ";
				headers += szSize;
				headers += L"\r\nContent-Type: application/x-www-form-urlencoded\r\n";
				post.SetAdditionalRequestHeaders(headers);

				//Set max timeout
				post.SetTimeouts(30000U, 30000U, 30000U, 30000U);

				//Execute the post request
				if(!post.SendHttpRequest(L"POST", true)) //If it failed log it
				{
					char answer[2048];
					sprintf(answer, "POST FAILED from: %s for id: %d, user: %s", clientIp, uniqueId, username);
					logger(answer);
					pECB->SetResponseType(pECB, rcAccessReject);
					return NO_ERROR;
				}
				else
				{
					//Set the time for our timeout loop
					int timeout = 30;

					//Pre-init the response var
					char * response;

					//Our timeout loop
					for(int i=1; i<=timeout; i++)
					{
						//Check if we received some content
						if((int) post.GetRawResponseReceivedContentLength() > 0)
						{
							//YEAHHH!! We received a response
							response = (char *) post.GetRawResponseContent();
							break; //Exit the loop
						}
						else if(i >= timeout)
						{
							//OOPS!! Still no response after our time out time
							pECB->SetResponseType(pECB, rcAccessReject); //Set our RADIUS response to reject
							return NO_ERROR; //Exit the complete function
						}
						else //Still no response and within the timeout
						{
							//Sleep for 1 second (1000 miliseconds)
							Sleep(1000);
							continue; //Continue the loop
						}
					}

					//Finally out of the loop

					//Check again if response is not empty
					if(sizeof(response) == 0)
					{
						//If so, exit
						pECB->SetResponseType(pECB, rcAccessReject); //Set our RADIUS response to reject
						return NO_ERROR; //Exit the complete function
					}

					//Produce a json document from the received content
					rapidjson::Document json;
					json.Parse(response);

					//Pre-init var
					int code;

					//Check if the received json contains the "result" key
					if(json.HasMember("result"))
					{
						//If so, Get it as an int
						if(json["result"].IsInt())
						{
							//Write it to the code var
							code = json["result"].GetInt();
						}
						else //If it is not an int or it failed deny the request
						{
							pECB->SetResponseType(pECB, rcAccessReject); //Set our RADIUS response to reject
							return NO_ERROR; //Exit the complete function
						}
					}
					else //If the result key doesn't exist, deny the request
					{
						pECB->SetResponseType(pECB, rcAccessReject); //Set our RADIUS response to reject
						return NO_ERROR; //Exit the complete function
					}

					//If the result code is 0 Allow the request
					if(code == 0)
					{
						char out[5000];
						sprintf(out, "Login SUCCES from %s for id: %d, user: %s", clientIp, uniqueId, username);
						logger(out);
						pECB->SetResponseType(pECB, rcAccessAccept); //Set our RADIUS response to accept
					}
					else //Log every other result code
					{
						char out[5000];
						sprintf(out, "Login FAILED from %s for id: %d, user: %s | result==> code: %d, text: %s", clientIp, uniqueId, username, code, json["resultText"].GetString());
						logger(out);
						pECB->SetResponseType(pECB, rcAccessReject); //Set our RADIUS response to reject
					}
				
				}
			
			}
		}
		else if (pECB->rcRequestType == rcAccountingRequest) //If request type is an accounting request
		{
			pECB->SetResponseType(pECB, rcAccountingResponse); //Then just answer it
		}
		else //If we get another request
		{
			pECB->SetResponseType(pECB, rcUnknown); //We don't know what to do with it
		}
	}
	return NO_ERROR; //Exit the complete function
}
