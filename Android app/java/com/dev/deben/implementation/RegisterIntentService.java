/*
 * Feel free to copy/use it for your own project.
 * Keep in mind that it took me several days/weeks, beers and asperines to make this.
 * So be nice, and give me some credit, I won't bite and it won't hurt you.
 *
 * Created by Deben Oldert
 */

package com.dev.deben.implementation;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;

public class RegisterIntentService extends IntentService {

    private static final String TAG = "RegIntentService";

    public RegisterIntentService() {
        super(TAG);
    }
    function fn = new function(this);

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            System.out.println("Service creating token");
            System.out.println("Token: "+token);

            sendRegistrationToServer(token);

        } catch (Exception e) {
            System.out.println("Failed to complete token refresh");
        }

    }
    private void sendRegistrationToServer(String token) throws IOException, JSONException {
        System.out.println("REGISTERING TOKEN");

        HashMap<String, String> set = new HashMap<>();
        set.put("notificationId", token);
        fn.writeSetting(set);
    }


}