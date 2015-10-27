package com.dev.deben.implementation;

import org.json.JSONException;
//import org.json.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContextWrapper;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import java.io.StringWriter;
import java.util.Map;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.DataOutputStream;

import android.provider.Settings.Secure;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.ConnectionResult;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import android.support.v4.app.NotificationCompat;
import android.app.TaskStackBuilder;
import android.app.PendingIntent;
import android.app.NotificationManager;
import android.content.Context;

/**
 * Created by Deben on 21-10-15.
 */
public class function {
    private static Context ctx;
    private static String settings;
    private static String file = "settings.json";

    function(Context init) {
        ctx = init;
    }

    public boolean writeSetting(HashMap<String, String> set) throws IOException, JSONException {
        String jsonText = settingFile();
        //System.out.println("JSON WRITE BEFORE: "+jsonText);
        if(jsonText != null) {
            Object obj = JSONValue.parse(jsonText);
            JSONObject json = (JSONObject) obj;
            for(Map.Entry<String, String> entry : set.entrySet()) {
                json.put(entry.getKey(), entry.getValue());
            }

            FileOutputStream fout = ctx.openFileOutput(file, ctx.MODE_PRIVATE);
            OutputStreamWriter outwr = new OutputStreamWriter(fout);

            StringWriter out = new StringWriter();
            json.writeJSONString(out);

            String txt = out.toString();
            settings = txt;

            outwr.write(txt);
            outwr.close();
            //System.out.println("JSON WRITE AFTER: " + txt);
            return true;
        }
        else{
            //System.out.println("JSON WRITE NULL");
            return false;
        }
    }
    public boolean writeSetting(String key, String value) throws IOException, JSONException {
        String jsonText = settingFile();
        if(jsonText != null) {
            Object obj = JSONValue.parse(jsonText);
            JSONObject json = (JSONObject) obj;
            json.put(key, value);

            FileOutputStream fout = ctx.openFileOutput(file, ctx.MODE_PRIVATE);
            OutputStreamWriter outwr = new OutputStreamWriter(fout);

            StringWriter out = new StringWriter();
            json.writeJSONString(out);

            String txt = out.toString();
            settings = txt;

            outwr.write(txt);
            outwr.close();
            return true;
        }
        else{
            return false;
        }
    }
    public void init() throws IOException, JSONException {
        settings = settingFile();
        System.out.println("INIT START");
        Object obj = JSONValue.parse(settings);
        JSONObject json = (JSONObject) obj;
        if(json.get("apiKey").equals("")) {
            System.out.println("INIT CREATE");

            Intent intent = new Intent(ctx, InstanceIdService.class);
            ctx.startService(intent);

            //InstanceIdService srv = new InstanceIdService();

            //HashMap<String, String> set = new HashMap<>();
            //set.put("apiKey", srv.getToken());

            //System.out.println("TOKEN: "+srv.getToken());

            //writeSetting(set);

            settings = settingFile();
        }
        System.out.println("INIT STOP");
    }

    public String readSetting(String key) throws JSONException, IOException {
            Object obj = JSONValue.parse(settings);
            JSONObject json = (JSONObject) obj;
            String value = null;
            if (json.get(key) != null) {
                value = (String) json.get(key);
            }
            return value;
    }
    public void logout() throws IOException, JSONException {
        JSONObject json = new JSONObject();
        json.put("function", "unregister");
        json.put("username", readSetting("username"));
        json.put("password", readSetting("password"));
        json.put("registerCode", readSetting("registerCode"));
        json.put("requestId", "0");

        makeRequest("POST", readSetting("serverUrl"), json.toJSONString());

        System.out.println("Logging out");
        File del = new File(ctx.getFilesDir().getAbsolutePath(), file);
        del.delete();
        redirect("Login");
    }

    private String settingFile() throws IOException, JSONException {
        ContextWrapper cw = new ContextWrapper(ctx);

        String jsonStr = null;

        System.out.println("Trying to read settings");

        try {
            FileInputStream fin = ctx.openFileInput(file);
            BufferedReader rd = new BufferedReader(new InputStreamReader(fin));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            jsonStr = sb.toString();
            rd.close();
            fin.close();
            System.out.println("Read settings");
        }
        catch(IOException e){
            FileOutputStream fout = ctx.openFileOutput(file, ctx.MODE_PRIVATE);
            OutputStreamWriter outwr = new OutputStreamWriter(fout);

            JSONObject json = new JSONObject();
            json.put("serviceType", "GCM");
            json.put("serviceNumber", "454250381809");
            json.put("notificationId", "");
            json.put("deviceId", Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID));
            json.put("username", "");
            json.put("password", "");
            json.put("registerCode", "");
            json.put("apiKey", "AIzaSyB67KpF-KSuZoPdnuy03TEIKRjHkBLEPpM");
            json.put("serverUrl", "http://192.168.2.240:8080/Implementation/SAS");
            json.put("requestId", "0");

            StringWriter out = new StringWriter();
            json.writeJSONString(out);

            jsonStr = out.toString();

            outwr.write(jsonStr);
            outwr.close();
        }
        System.out.println("return settings: "+jsonStr);
        return jsonStr;

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
    public String genRegCode(String str) {
        String first = str.substring(0, 1);
        String last = str.substring(str.length()-1);
        int firstCode = getCharCode(first);
        int lastCode = getCharCode(last);

        System.out.println("first: "+first+", "+firstCode);
        System.out.println("first: "+last+", "+lastCode);

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

    public String makeRequest(String type, String url, String body) throws IOException {
        System.out.println(url);
        System.out.println(body);
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod(type);
                con.setRequestProperty("content-type", "application/json");

                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(body);
                wr.flush();
                wr.close();

                BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                String response = sb.toString();

                rd.close();
                con.disconnect();

                return response;

    }
    public void redirect(String act) {
        Intent intent;
        switch(act) {
            case "Main":
                intent = new Intent(ctx, MainActivity.class);
                break;
            case "Login":
                intent = new Intent(ctx, LoginActivity.class);
                break;
            default:
                intent = new Intent(ctx, LoginActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }
    public int notifier(String title, String msg) {
        System.out.println("BUILDING NOTIFICATION");
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pi = PendingIntent.getActivity(ctx, 0, new Intent(ctx, MainActivity.class), 0);
        int id = (int)System.currentTimeMillis();
        Notification mBuilder = new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.drawable.action_logo)
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setAutoCancel(true)
                        .setContentIntent(pi)
                        .build();
        nMgr.notify(id, mBuilder);
        return id;

    }
    public void cancelNotify(int id) {
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(id);
    }
}