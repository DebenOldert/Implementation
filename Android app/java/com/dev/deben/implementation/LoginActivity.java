/*
 * Feel free to copy/use it for your own project.
 * Keep in mind that it took me several days/weeks, beers and asperines to make this.
 * So be nice, and give me some credit, I won't bite and it won't hurt you.
 *
 * Created by Deben Oldert
 */

package com.dev.deben.implementation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class LoginActivity extends AppCompatActivity {

    function fn = new function(this);
    TextView err;
    Button login;
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

        err = (TextView) findViewById(R.id.error);
        err.setVisibility(View.INVISIBLE);
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

    private void login(String usr, String pass, String cod) throws IOException, JSONException {
        final String username = usr;
        final String password = pass;
        final String reqCode = cod;
        final ProgressDialog progress = new ProgressDialog(this);
        err.setVisibility(View.INVISIBLE);
        err.setText("");
        if(username == null || username.equals("") && username.length() < 5) {
            err.setText("Username too short");
            err.setVisibility(View.VISIBLE);
            return;
        }
        if(password == null || password.equals("") && password.length() < 5) {
            err.setText("Password too short");
            err.setVisibility(View.VISIBLE);
            return;
        }
        if(reqCode == null || reqCode.equals("") || reqCode.length() != 4) {
            err.setText("Invalid register code");
            err.setVisibility(View.VISIBLE);
            return;
        }
            progress.setTitle("Registering your device");
            progress.setMessage("Processing...");
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
            json.put("registerCode", reqCode);
            json.put("username", username);
            json.put("password", password);
            json.put("userInfo", info);

        AsyncHttpClient client = new AsyncHttpClient();

        client.post(this, fn.readSetting("serverUrl"), new StringEntity(json.toJSONString()), "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int code, Header[] headers, byte[] responseBody, Throwable error) {
                err.setTextColor(Color.parseColor("#ff1700"));
                err.setText("HTTP ERROR: " + code);
                err.setVisibility(View.VISIBLE);
                progress.dismiss();
            }

            @Override
            public void onSuccess(int code, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                Object obj = JSONValue.parse(res);
                JSONObject response = (JSONObject) obj;

                if (response.get("result") != null && response.get("result").equals("0") || Integer.parseInt(response.get("result").toString()) == 0) {
                    HashMap<String, String> set = new HashMap<>();
                    set.put("username", username);
                    set.put("password", password);
                    set.put("registerCode", reqCode);
                    set.put("requestId", "0");
                    try {
                        fn.writeSetting(set);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    progress.dismiss();
                    fn.redirect("Main");
                    err.setVisibility(View.VISIBLE);
                    progress.dismiss();
                } else {
                    progress.dismiss();
                    err.setText("Error " + response.get("result") + ": " + response.get("resultText"));
                    err.setVisibility(View.VISIBLE);
                    return;
                }
            }
        });
        return;
    }
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000)
                        .show();
                err.setText("Service must be up to date in order to use this app");
                err.setVisibility(View.VISIBLE);
                login.setEnabled(false);
            } else {
                err.setText("Device not supported");
                err.setVisibility(View.VISIBLE);
                login.setEnabled(false);
                finish();
            }
            return false;
        }
        return true;
    }
}
