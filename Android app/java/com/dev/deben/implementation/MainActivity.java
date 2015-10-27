package com.dev.deben.implementation;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.io.StringWriter;


public class MainActivity extends AppCompatActivity {

    function fn = new function(this);
    public boolean active = true;
    Button accept;
    Button deny;
    TextView status;
    TextView desc;

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onPause() {
        super.onPause();
        active = false;
    }
    @Override
    public void onResume() {
        super.onResume();
        active = true;
        accept = (Button) findViewById(R.id.Accept);
        deny = (Button) findViewById(R.id.Deny);
        status = (TextView) findViewById(R.id.status);
        desc = (TextView) findViewById(R.id.descriptor);
        try {
            if(fn.readSetting("requestId").equals("0")) {
                desc.setText(R.string.No_req);
                accept.setEnabled(false);
                deny.setEnabled(false);
            }
            else {
                desc.setText(R.string.req);
                accept.setEnabled(true);
                deny.setEnabled(true);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        accept = (Button) findViewById(R.id.Accept);
        deny = (Button) findViewById(R.id.Deny);
        status = (TextView) findViewById(R.id.status);
        try {
            if(fn.readSetting("requestId").equals("0")) {
                status.setText("@string/No_req");
                accept.setEnabled(false);
                deny.setEnabled(false);
            }
            else {
                status.setText("@string/req");
                accept.setEnabled(true);
                deny.setEnabled(true);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }


        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    buildReply(true, accept, deny);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

            }
        });
        deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    buildReply(false, accept, deny);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

       menu.add("Unregister");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println(item.getItemId());
        switch(item.getItemId()) {
            case 0:
                try {
                    fn.logout();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
        return true;

    }



    private void buildReply(boolean grand, Button accept, Button deny) throws JSONException, IOException {
        accept.setEnabled(false);
        deny.setEnabled(false);

        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle((grand ? "Approving" : "Cancelling") + " request");
        progress.setMessage("Processing...");
        progress.setCancelable(false);
        progress.show();

        JSONObject json = new JSONObject();
        json.put("function", "confirm");
        json.put("requestId", fn.readSetting("requestId"));
        json.put("deviceId", fn.readSetting("deviceId"));
        json.put("apiKey", fn.readSetting("apiKey"));
        json.put("notificationId", fn.readSetting("notificationId"));
        json.put("confirmation", grand ? "approved" : "cancelled");

        StringWriter out = new StringWriter();
        json.writeJSONString(out);

        String response = fn.makeRequest("POST", fn.readSetting("serverUrl"), out.toString());
        Object obj = JSONValue.parse(response);
        JSONObject res = (JSONObject) obj;



        if(res.get("result").equals("0") || Integer.parseInt(res.get("result").toString()) == 0) {
            status.setText("VPN request successfully " + (grand ? "APPROVED" : "CANCELLED"));
            if(grand) {
                status.setTextColor(Color.parseColor("#04ff00"));
            }
            else {
                status.setTextColor(Color.parseColor("#DF7401"));
            }
            fn.writeSetting("requestId", "0");
        }
        else {
            status.setText("Something went wrong, " + res.get("result").toString() + "\n" + res.get("resultText").toString());
            status.setTextColor(Color.parseColor("#ff1700"));
        }
        status.setVisibility(View.VISIBLE);
        accept.setEnabled(true);
        deny.setEnabled(true);

        progress.dismiss();
    }


}
