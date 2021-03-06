/*
 * Feel free to copy/use it for your own project.
 * Keep in mind that it took me several days/weeks, beers and asperines to make this.
 * So be nice, and give me some credit, I won't bite and it won't hurt you.
 */

import java.util.HashMap;

/**
 *
 * @author Deben Oldert
 */
public class ErrorCode {
    public String getCodeText(int id) {
        HashMap<Integer, String> list = new HashMap<>();
        /* Runtime errors */
        list.put(-1, "Unknown error code");
        list.put(0, "OK");
        list.put(1, "Refused");
        list.put(2, "User needs device registration");
        list.put(5, "Invalid code");
        list.put(50, "Failed to send mail");
        list.put(51, "Incorrect mail adress");
        list.put(52, "Mail not equal");
        list.put(80, "Failed to connect to LDAP server");
        list.put(81, "Error reading attribute");
        list.put(82, "Error writing attribute");
        list.put(83, "Failed to register device");
        
        // Data errors
        list.put(500, "No data given");
        list.put(555, "Unsupported function");
        list.put(560, "Missing variables");
        
        //DB errors
        list.put(600, "DB connection coun\'t be established");
        list.put(601, "Failed to write to DB");
        list.put(602, "Failed to read from DB");
        
        //LDAP errors
        list.put(699, "User not found");
        list.put(700, "OK");
        list.put(749, "Username / password incorrect");
        
        
        /* User generated errors */
        list.put(900, "GET request not Allowed");
        list.put(901, "POST request not Allowed");
        list.put(950, "Process timed out");
        
        if(list.get(id) != null) {
            return list.get(id);
        }
        else {
            return list.get(-1);
        }
    }
}
