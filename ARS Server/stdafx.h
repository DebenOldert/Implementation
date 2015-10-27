// stdafx.h : include file for standard system include files,
// or project specific include files that are used frequently, but
// are changed infrequently
//

#pragma once


#define WIN32_LEAN_AND_MEAN             // Exclude rarely-used stuff from Windows headers
// Windows Header Files:
#include <windows.h>

#ifdef NPS_DLL_EXAMPLE_EXPORTS
#define NPS_DLL_EXAMPLE_API __declspec(dllexport)
#else
#define NPS_DLL_EXAMPLE_API __declspec(dllimport)
#endif
#include <Authif.h>


#ifdef __cplusplus
extern "C" {
#endif
	DWORD NPS_DLL_EXAMPLE_API WINAPI RadiusExtensionProcess2(
		_Inout_  PRADIUS_EXTENSION_CONTROL_BLOCK pECB
		);
#ifdef __cplusplus
}
#endif

//void logger(const char * msg);

