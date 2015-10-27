package com.dev.deben.implementation;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceIDListenerService;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import android.content.Context;

/**
 * Created by Deben on 22-10-15.
 */
public class InstanceIdService extends InstanceIDListenerService {
    Context ctx = this;




    function fn = new function(ctx);

    public String getIid() {
        return InstanceID.getInstance(ctx).getId().toString();
    }

    public String getToken() throws IOException {
        return "";
        /*String authEntity = "implementation-51b96";
        String scope = "GCM";
        System.out.println("GENERATING TOKEN");
        try {
            return InstanceID.getInstance(ctx).getToken("implementation-51b96", "GCM").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";*/
    }

};