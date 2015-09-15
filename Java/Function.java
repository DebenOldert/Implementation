
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Deben
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
                //System.out.println(key+", "+jsonObject.get(key));
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
                    //System.out.println("userInfo: "+key+", "+userObject.get(key));
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
                return "https://android.googleapis.com/gcm/send";
            case "APNS":
                return "http://apple.com";
            default:
                return null;
        }
    }
    public String makeRequest(String type, String url, String body) throws MalformedURLException, IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        
        switch(type) {
            case "GET":
                return "";
            case "POST":
                con.setRequestMethod("POST");
		con.setRequestProperty("content-type", "application/json");
                
                con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(body);
		wr.flush();
		wr.close();
                
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
    
}
