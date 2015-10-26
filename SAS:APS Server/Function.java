/*
 * Feel free to copy/use it for your own project.
 * Keep in mind that it took me several days/weeks, beers and asperines to make this.
 * So be nice, and give me some credit, I won't bite and it won't hurt you.
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Deben Oldert
 */
public class Function {
    
    
    public HashMap defragJSON(String json) throws ParseException {
        
        String[] checkList = {"function",
                              "requestId",
                              "username",
                              "password",
                              "userInfo",
                              "serviceType",
                              "serviceNumber",
                              "apiKey",
                              "deviceId",
                              "notificationId",
                              "registerCode",
                              "serverURL",
                              "appURL",
                              "storeURL",
                              "confirmation",
                              "result",
                              "resultText",
                              "email",
                              "mail",
                              "subject",
                              "text"};
        
        HashMap<String, String> array = new HashMap();
        
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(json);
        JSONObject jsonObject = (JSONObject) obj;
        
        boolean userInfo = jsonObject.containsKey("userInfo");
        System.out.println(obj);
        for(String key : checkList) {
            if(key.equals("confirmation") && jsonObject.containsKey("confirmation")) {
                if(!jsonObject.get(key).equals("approved")) {
                    jsonObject.remove(key);
                    jsonObject.put(key, "cancelled");
                }
            }
            if(jsonObject.get(key) != null && !jsonObject.get(key).getClass().getName().equals("org.json.simple.JSONObject")) {
                array.put(key, (String) jsonObject.get(key).toString());
            }
            if(userInfo) {
                JSONObject userObject = (JSONObject) jsonObject.get("userInfo");
                if(userObject.get(key) != null) {
                    if(key.equals("mail")) {
                        array.put("email", (String) userObject.get(key));
                        System.out.println("MAIL RENAME");
                        continue;
                    }
                    array.put(key, (String) userObject.get(key).toString());
                }
            }
        }
        return array;
    }
    public String getBody(BufferedReader br) throws IOException {
        String tmp, body = "";
        
        while(null != (tmp = br.readLine())) {
            body += tmp;
        }
        return body;
    }
    
    public String getURL(String server) {
        switch(server) {
            case "ARS":
                return "http://localhost:8080/Implementation/ARS";
            case "SAS":
                return "http://localhost:8080/Implementation/SAS";
            case "APS":
                return "http://localhost:8080/Implementation/APS";
            case "APP":
                return "http://test.com";
            case "STORE":
                return "http://google.com";
            case "GCM":
                return "https://gcm-http.googleapis.com/gcm/send";
            case "APNS":
                return "http://apple.com";
            default:
                return null;
        }
    }
    public String makeRequest(String type, String url, String body) throws MalformedURLException, IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        System.out.println(body);
        
        
        switch(type) {
            case "GET":
                return "";
            case "POST":
                con.setRequestMethod("POST");
		con.setRequestProperty("content-type", "application/json");
                if(url.contains("gcm")) {
                    con.setRequestProperty("Authorization", "key=AIzaSyB67KpF-KSuZoPdnuy03TEIKRjHkBLEPpM");
                }
                
                con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(body);
		wr.flush();
		wr.close();
                if(con.getResponseCode() != 200) {
                    System.err.println(con.getResponseCode());
                    return String.valueOf(con.getResponseCode());
                }
                
                BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuilder response = new StringBuilder();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
                
                System.out.println("============= makeRequest =============>> FUNCTION");
        System.out.print(response.toString());
        System.out.println("********************************************************");
                
                return response.toString();
            default:
                return null;
        }
    }
    public String genRegCode(String str) {
        String first = str.substring(0, 1);
        String last = str.substring(str.length()-1);
        int firstCode = getCharCode(first);
        int lastCode = getCharCode(last);
        if(firstCode < 27 && firstCode > 0) {
            first = ""+firstCode;
        }
        else {
            return null;
        }
        if(lastCode < 27 && lastCode > 0) {
            last = ""+lastCode;
        }
        else {
            return null;
        }
        if(first.length() < 2) {
            first = "0"+first;
        }
        if(last.length() < 2) {
            last = "0"+last;
        }
        return first+last;
    }
    public boolean checkRegCode(String code, String str) {
        String newCode = genRegCode(str);
        return code.equals(newCode);
    }
    
    private int getCharCode(String letter) {
        letter = letter.toLowerCase();
        switch(letter) {
            case "a":
                return 1;
            case "b":
                return 2;
            case "c":
                return 3;
            case "d":
                return 4;
            case "e":
                return 5;
            case "f":
                return 6;
            case "g":
                return 7;
            case "h":
                return 8;
            case "i":
                return 9;
            case "j":
                return 10;
            case "k":
                return 11;
            case "l":
                return 12;
            case "m":
                return 13;
            case "n":
                return 14;
            case "o":
                return 15;
            case "p":
                return 16;
            case "q":
                return 17;
            case "r":
                return 18;
            case "s":
                return 19;
            case "t":
                return 20;
            case "u":
                return 21;
            case "v":
                return 22;
            case "w":
                return 23;
            case "x":
                return 24;
            case "y":
                return 25;
            case "z":
                return 26;
            default:
                return 0;
        }
    }
    
}
