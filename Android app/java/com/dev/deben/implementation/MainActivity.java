/*
 * Feel free to copy/use it for your own project.
 * Keep in mind that it took me several days/weeks, beers and asperines to make this.
 * So be nice, and give me some credit, I won't bite and it won't hurt you.
 *
 * Created by Deben Oldert
 */

package com.dev.deben.implementation;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;

import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity {

    function fn = new function(this);

    public boolean active = true;
    Button accept;
    Button deny;
    TextView status;
    TextView desc;
    Context ctx = this;
    boolean opt;

    @Override
    public void onBackPressed() {
        return;
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
                    opt = true;
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
                    opt = false;
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

        final ProgressDialog progress = new ProgressDialog(this);
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

        AsyncHttpClient client = new AsyncHttpClient();

        client.post(ctx, fn.readSetting("serverUrl"), new StringEntity(json.toJSONString()), "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int code, Header[] headers, byte[] responseBody, Throwable error) {
                status.setTextColor(Color.parseColor("#ff1700"));
                status.setText("HTTP ERROR: " + code);
                status.setVisibility(View.VISIBLE);
                progress.dismiss();
            }

            @Override
            public void onSuccess(int code, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                Object obj = JSONValue.parse(res);
                JSONObject response = (JSONObject) obj;

                if (response.get("result") != null && response.get("result").equals("0") || Integer.parseInt(response.get("result").toString()) == 0) {
                    status.setTextColor(Color.parseColor("#04ff00"));
                    status.setText("SUCCESS");

                    try {
                        fn.writeSetting("requestId", "0");
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    finish();
                    startActivity(getIntent());
                } else {
                    status.setText(response.get("result").toString() + "\n" + response.get("resultText").toString());
                    status.setTextColor(Color.parseColor("#ff1700"));
                }
                status.setVisibility(View.VISIBLE);
                progress.dismiss();
                fn.cancelNotify();
            }
        });
    }


}
