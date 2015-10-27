package com.dev.deben.implementation;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.ProgressDialog;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import android.os.StrictMode;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class LoginActivity extends AppCompatActivity {

    function fn = new function(this);
    TextView error;
    Button login;
    String regCode;
    EditText userField;
    EditText passField;
    EditText codeField;

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        error = (TextView) findViewById(R.id.error);
        error.setVisibility(View.INVISIBLE);
        login = (Button) findViewById(R.id.login);
        userField = (EditText) findViewById(R.id.username);
        passField = (EditText) findViewById(R.id.password);
        codeField = (EditText) findViewById(R.id.regCode);

        Uri param = getIntent().getData();

        if(param != null) {
            System.out.println("URI=" + param.toString());
            if (param.getQueryParameter("rid") != null) {
                HashMap<String, String> set = new HashMap<>();
                set.put("requestId", param.getQueryParameter("rid"));
                try {
                    fn.writeSetting(set);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
            if (param.getQueryParameter("rcd") != null) {
                codeField.setText(param.getQueryParameter("rcd"));
            }
        }

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        if(checkPlayServices()) {
            Intent intent = new Intent(this, RegisterIntentService.class);
            startService(intent);
        }

        try {
            fn.init();
            if(!fn.readSetting("username").equals("") && !fn.readSetting("registerCode").equals("")) {
                if(fn.checkRegCode(fn.readSetting("registerCode"), fn.readSetting("username"))) {
                    //USER ALREADY LOGGED IN
                    fn.redirect("Main");
                }
                else {
                    //NO LOGIN
                }
            }



        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        Button button = (Button) findViewById(R.id.login);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                try {
                    login(userField.getText().toString(), passField.getText().toString(), codeField.getText().toString());
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void login(String username, String password, String code) throws IOException, JSONException {
        error.setVisibility(View.INVISIBLE);
        error.setText("");
        if(username == null || username.equals("") && username.length() < 5) {
            error.setText("Username too short");
            error.setVisibility(View.VISIBLE);
            return;
        }
        if(password == null || password.equals("") && password.length() < 5) {
            error.setText("Password too short");
            error.setVisibility(View.VISIBLE);
            return;
        }
        if(code == null || code.equals("") || code.length() != 4) {
            error.setText("Invalid register code");
            error.setVisibility(View.VISIBLE);
            return;
        }

            ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle("Registering");
            progress.setMessage("Processing request...");
            progress.setCancelable(false);
            progress.show();

            JSONObject info = new JSONObject();
            info.put("serviceType", "GCM");
            info.put("serviceNumber", fn.readSetting("serviceNumber"));
            info.put("deviceId", fn.readSetting("deviceId"));
            info.put("notificationId", fn.readSetting("notificationId"));
            info.put("apiKey", fn.readSetting("apiKey"));


            JSONObject json = new JSONObject();
            json.put("function", "register");
            json.put("requestId", fn.readSetting("requestId"));
            json.put("registerCode", code);
            json.put("username", username);
            json.put("password", password);
            json.put("userInfo", info);

            StringWriter out = new StringWriter();
            json.writeJSONString(out);

            String response = fn.makeRequest("GET", fn.readSetting("serverUrl"), out.toString());
            System.out.println(response);
            if(response.length() == 0 || response == null || response == "") {
                progress.dismiss();
                error.setText("Request Failed. Try again later");
                error.setVisibility(View.VISIBLE);
                return;
            }
            else {
                Object obj = JSONValue.parse(response);
                JSONObject res = (JSONObject) obj;
                if(res.get("result").equals("0") || Integer.parseInt(res.get("result").toString()) == 0) {

                    HashMap<String, String> set = new HashMap<>();
                    set.put("username", username);
                    set.put("password", password);
                    set.put("registerCode", code);
                    set.put("requestId", "0");
                    fn.writeSetting(set);
                    progress.dismiss();
                    fn.redirect("Main");
                }
                else {
                    progress.dismiss();
                    error.setText("Error "+res.get("result")+": "+res.get("resultText"));
                    error.setVisibility(View.VISIBLE);
                    return;
                }
            }
            return;
    }
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000)
                        .show();
                error.setText("Service must be up to date in order to use this app");
                error.setVisibility(View.VISIBLE);
                login.setEnabled(false);
            } else {
                error.setText("Device not supported");
                error.setVisibility(View.VISIBLE);
                login.setEnabled(false);
                finish();
            }
            return false;
        }
        return true;
    }
}
