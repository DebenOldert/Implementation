Function GenerateOID()
    'Initializing Variables
    Dim guidString, oidPrefix
    Dim guidPart0, guidPart1, guidPart2, guidPart3, guidPart4, guidPart5, guidPart6
    Dim oidPart0, oidPart1, oidPart2, oidPart3, oidPart4, oidPart5, oidPart6
    On Error Resume Next
    'Generate GUID
    Set TypeLib = CreateObject("Scriptlet.TypeLib")
    guidString = TypeLib.Guid
    'If no network card is available on the machine then generating GUID can result with an error.
    If Err.Number <> 0 Then
        Wscript.Echo "ERROR: Guid could not be generated, please ensure machine has a network card."
        Err.Clear
        WScript.Quit
    End If
    'Stop Error Resume Next
    On Error GoTo 0
    'The Microsoft OID Prefix used for the automated OID Generator
    oidPrefix = "1.2.840.113556.1.8000.2554"
    'Split GUID into 6 hexadecimal numbers
    guidPart0 = Trim(Mid(guidString, 2, 4))
    guidPart1 = Trim(Mid(guidString, 6, 4))
    guidPart2 = Trim(Mid(guidString, 11, 4))
    guidPart3 = Trim(Mid(guidString, 16, 4))
    guidPart4 = Trim(Mid(guidString, 21, 4))
    guidPart5 = Trim(Mid(guidString, 26, 6))
    guidPart6 = Trim(Mid(guidString, 32, 6))
    'Convert the hexadecimal to decimal
    oidPart0 = CLng("&H" & guidPart0)
    oidPart1 = CLng("&H" & guidPart1)
    oidPart2 = CLng("&H" & guidPart2)
    oidPart3 = CLng("&H" & guidPart3)
    oidPart4 = CLng("&H" & guidPart4)
    oidPart5 = CLng("&H" & guidPart5)
    oidPart6 = CLng("&H" & guidPart6)
    'Concatenate all the generated OIDs together with the assigned Microsoft prefix and return
    GenerateOID = oidPrefix & "." & oidPart0 & "." & oidPart1 & "." & oidPart2 & "." & oidPart3 & _
        "." & oidPart4 & "." & oidPart5 & "." & oidPart6
End Function


Set objFSO=CreateObject("scripting.FileSystemObject")
outFile="C:/X500UID.txt"
Set objFile = objFSO.createTextFile(outFile, true)
objFile.Write GenerateOID &VBCRLF
objFile.Close


'Output the resulted OID with best practice info
Wscript.Echo "X500 UID saved in C:/X500UID.txt"